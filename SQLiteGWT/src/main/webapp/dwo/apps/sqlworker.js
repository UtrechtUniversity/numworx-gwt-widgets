/**
 * sql-worker.js
 */
importScripts("https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.6.1/sql-wasm.js");

let db;

self.output = function(ch) {
    let string = String.fromCharCode(ch);
    self.postMessage(JSON.stringify({ "output": string }));
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

self.input = async function() {
    let id = self.makeid(8);
    self.postMessage(JSON.stringify({ "request": "input", "id": id }));
    const response = await new Promise((resolve, reject) => {
        const handleMessage = (event) => {
            var data = JSON.parse(event.data);
            if (data.id === id) {
                resolve(data.input);
                self.removeEventListener('message', handleMessage);
            }
        };
        self.addEventListener('message', handleMessage);
    });
    return response;
};

async function loadSQL(databaseFile) {
    const SQL = await initSqlJs({
        locateFile: (file) => `https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.6.1/${file}`
    });
    
    
    // Fetch the predefined database file
    const response = await fetch(databaseFile);
    const buffer = await response.arrayBuffer();
    db = new SQL.Database(new Uint8Array(buffer));
   
}
let predefinedDatabaseFile = "https://www.fi.uu.nl/dwo/resources/sqlite_danilo.db"; 
// Replace with your actual URL https://www.fi.uu.nl/dwo/resources/danilo_setup.sql

   
let sqlReadyPromise = loadSQL(predefinedDatabaseFile);


self.onmessage = async (event) => {
    // Make sure loading is done
    await sqlReadyPromise;
    const data = JSON.parse(event.data);
    if (typeof data === 'string') {
        self.resolver(data);
        self.resolver = function(x) {};
        return;
}

    if (typeof data === 'string') {
        self.resolver(data);
        self.resolver = function(x) {};
        return;
    }
    
    const { id, python } = data;
     query=python;
    // Now is the easy part, the one that is similar to working in the main thread:
    try {
        const result = db.exec(query);
        self.postMessage(JSON.stringify({ result, id }));
    } catch (error) {
        self.postMessage(JSON.stringify({ error: error.message, id }));
    }
};

