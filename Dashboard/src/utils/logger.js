/* eslint-disable no-console */

const fs = require('fs').promises;

let basePath = 'src/logs/';
let errLogPath = basePath + 'error.log';
let eventLogPath = basePath + 'event.log';

const startTime = Date.now();

fs.mkdir(`src/logs/data`, { recursive: true });

export async function logError(errorMsg, obj) {
    let msg = `${new Date().toLocaleTimeString()} :: ${errorMsg}`;
    console.log('ERROR', msg);
    if(obj) {
        console.log('ERROR Object', obj);
        console.log('');
        console.log('');
    }
    msg += '\n';

    return fs.appendFile(errLogPath, msg)
        .catch(err => {
            console.log(err);
        });
}

export async function logEvent(event) {
    let msg = `${new Date().toLocaleTimeString()} :: ${event}`;
    console.log('INFO ', msg);
    msg += '\n';

    if(eventLogPath === null) {
        console.log('DID NOT LOG TO FILE');
        return;
    }
    return fs.appendFile(eventLogPath, msg)
        .catch(err => {
            console.log(err);
        });
}

export async function logData(key, data) {
    if (key.indexOf('/SmartDashboard/') !== -1) {
        key = key.replace('/SmartDashboard/', '');
    }

    const dataLogPath = `${basePath}/data/${key}.log`
    data = `${data}|${Date.now() - startTime},`;
    return fs.appendFile(dataLogPath, data)
        .catch(err => {
            console.log(err);
        });
}

export async function endMatchProcessing() {
    const dateStr = `${new Date().getFullYear()}-${new Date().getMonth()+1}-${new Date().getDate()}`;

    function _createMatchFolder(num) {
        fs.access(`src/logs/matches/${dateStr}/match-${num}`)
            .then(() => {
                _createMatchFolder(num+1)
            })
            .catch(async (err) => {

                const postMatchPath = `src/logs/matches/${dateStr}/match-${num}/`;
                await fs.mkdir(postMatchPath, { recursive: true });
                fs.rename(errLogPath, postMatchPath + 'error.log');
                fs.rename(eventLogPath, postMatchPath + 'event.log');
                const dataPath = 'src/logs/data/';
                let dirs = await fs.readdir(dataPath);
                dirs.sort();

                dirs.forEach(async (file) => {
                    let data = await fs.readFile(dataPath + file);
                    await fs.appendFile(postMatchPath + 'dataFile.csv', `${file.substring(0, file.length - 4)},${data}\n`);
                    await fs.unlink(dataPath + file);
                });
            });
    }
    _createMatchFolder(0);
}
