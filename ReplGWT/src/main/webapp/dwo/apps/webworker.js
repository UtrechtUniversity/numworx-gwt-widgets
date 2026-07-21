/**
 * webworker.js
 */
// Setup your project to serve `py-worker.js`. You should also serve
// `pyodide.js`, and all its associated `.asm.js`, `.json`,
// and `.wasm` files as well:
importScripts("https://cdn.jsdelivr.net/pyodide/v0.23.4/full/pyodide.js");
importScripts("deploy.jsp");

self.state = { bytes: [], cnt: 0, off: 0, code: ""}

self.output = function(ch) {

	if (ch < 0) {
		ch = ch & 0xFF; // make 128..255
		if ( (ch & 0x40) == 0) { // continuation byte
			self.state.bytes[self.state.off++] = ch & 0x3F;
			if (self.state.off >= self.state.cnt) {
				ch = self.state.bytes[0];
				for(i = 1; i < self.state.cnt; i ++) {
					ch = (ch << 6) + self.state.bytes[i];
				}
				self.state.bytes = []
				self.state.off = 0;
				self.state.cnt = 0;
				if (ch > 0xFFFF) {
				    ch -= 0x10000;
					var lo = ch & 0x3FF;
					var hi = ch >>> 10;
					let string = String.fromCharCode(hi|0xD800) + String.fromCharCode(lo|0xDC00);
					self.postMessage(JSON.stringify( { "output": string } ))
					return;
				}				
			} else { 
				return;
			}	
		} else switch (ch & 0xF0) {
		case 0xC0: 
		case 0xD0:
			self.state.bytes = [ ch & 0x1F ]; // 11 bits
			self.state.off = 1;
			self.state.cnt = 2;
			return;
		case 0xE0:
			self.state.bytes = [ ch & 0xF ]; // 16 bits
			self.state.off = 1;
			self.state.cnt = 3;
			return;	
		case 0xF0:
			self.state.bytes = [ ch & 0xF ]; // 22 bits of 21 bits??
			self.state.off = 1;
			self.state.cnt = 4;
			return;
		}
	}

	let string = String.fromCharCode(ch);
	self.postMessage(JSON.stringify( { "output": string } ))
}

self.makeid = function(length) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    let counter = 0;
    while (counter < length) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
      counter += 1;
    }
    return result;
}

    self.restartScene = async () => self.pyodide.runPythonAsync(`
        import turtle
        turtle.restart()
      `);




self.input = function() {
 
  var id = self.makeid(8);
  self.postMessage(JSON.stringify( { "request": "input", "id": id } ));
  const request = new XMLHttpRequest();
  // `false` makes the request synchronous
  do {
  	request.open('GET', '/dwo/apps/get_input/'+id, false);
  	request.send(null);
  	console.log('status', request.status);
  } while (request.status == 404)
  const del = new XMLHttpRequest();
  del.open("DELETE", "/dwo/apps/get_input/"+id, true);
  del.send(null); // send and forget....
  return request.responseText;
}

     self.showScene = async () => self.pyodide.runPythonAsync(`
        import turtle
        import basthon

        svg_dict = turtle.Screen().show_scene()
        basthon.kernel.display_event({ "display_type": "turtle", "content": svg_dict })
      `);

    self.syncScene = () => self.pyodide.runPython(`
        import turtle
        import basthon

        svg_dict = turtle.Screen().show_scene()
        basthon.kernel.display_event({ "display_type": "turtle", "content": svg_dict })
      `);


self.makeObject = (x) => {
  if (x instanceof Map) {
    return Object.fromEntries(Array.from(
      x.entries(),
      ([k, v]) => [k, self.makeObject(v)]
    ))
  } else if (x instanceof Array) {
    return x.map(self.makeObject);
  } else {
    return x;
  }
}



async function loadPyodideAndPackages() {
  self.pyodide = await loadPyodide();
  self.pyodide.setStdout( { isatty: true, raw: self.output })
  self.pyodide.setStderr( { isatty: true, raw: self.output })
  self.pyodide.setStdin(  { isatty: true, stdin: self.input, autoEOF: true })
 // FIXME make OPTIONAL????
        var fakeBasthonPackage = {
        kernel: {
          display_event: (e) => {
          	let js = self.makeObject(e.toJs());
          	let st = JSON.stringify(js);
          	self.postMessage(st);
          }
          ,
          locals: () => self.pyodide.runPython("globals()"),
        },
      };
  	self.pyodide.registerJsModule("basthon", fakeBasthonPackage)
    await self.pyodide.loadPackage(deploy + "replgwt/turtle-0.0.1-py3-none-any.whl")
  
}
let pyodideReadyPromise = loadPyodideAndPackages();

self.onmessage = async (event) => {
  // make sure loading is done
  await pyodideReadyPromise;
 
  var data = JSON.parse(event.data)
  if (typeof data == 'string') {
  	self.resolver(data);
  	self.resolver = function(x) {};
  	return;	
  }
  // Don't bother yet with this line, suppose our API is built in such a way:
  const { id, python, ...context } = (data);
  // The worker copies the context in its own "memory" (an object mapping name to values)
  for (const key of Object.keys(context)) {
    self[key] = context[key];
  }
  if ( self['hasTurtle'] )
  	await self.restartScene();
  
  // Now is the easy part, the one that is similar to working in the main thread:
  try {
    await self.pyodide.loadPackagesFromImports(python); // this wil install numpy, etc....
    let results = await self.pyodide.runPythonAsync(python);
  	if ( self['hasTurtle'] ) await self.showScene(); // ook optional
    self.postMessage(JSON.stringify({ results, id }));
  } catch (error) {
    self.postMessage(JSON.stringify({ error: error.message, id }));
  }
};