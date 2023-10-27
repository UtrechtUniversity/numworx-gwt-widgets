/**
 * webworker.js
 */
// Setup your project to serve `py-worker.js`. You should also serve
// `pyodide.js`, and all its associated `.asm.js`, `.json`,
// and `.wasm` files as well:
importScripts("https://cdn.jsdelivr.net/pyodide/v0.23.4/full/pyodide.js");

self.output = function(ch) {
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

async function loadPyodideAndPackages() {
  self.pyodide = await loadPyodide();
  await self.pyodide.loadPackage(["numpy", "pytz"]);
  self.pyodide.setStdout( { isatty: true, raw: self.output })
  self.pyodide.setStderr( { isatty: true, raw: self.output })
  self.pyodide.setStdin(  { isatty: true, stdin: self.input, autoEOF: true })
  
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
  // Now is the easy part, the one that is similar to working in the main thread:
  try {
    await self.pyodide.loadPackagesFromImports(python);
    let results = await self.pyodide.runPythonAsync(python);
    self.postMessage(JSON.stringify({ results, id }));
  } catch (error) {
    self.postMessage(JSON.stringify({ error: error.message, id }));
  }
};