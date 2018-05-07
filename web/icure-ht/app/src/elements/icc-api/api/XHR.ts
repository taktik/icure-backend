export module XHR {

    export class Header {
        header: string;
        data: string;

        constructor(header: string, data: string) {
            this.header = header;
            this.data = data;
        }
    }

export class Data {
    headers: string;
    body: JSON|Array<JSON>;
    text: string;
    type: string;
    status: number;
    statusText: string;

    constructor(jsXHR: XMLHttpRequest) {
        this.headers = jsXHR.getAllResponseHeaders();
        if(jsXHR.responseType == "json" || jsXHR.responseType == ""){
            this.body = JSON.parse(jsXHR.response);
        }else{
            this.body = jsXHR.response;
        }
        this.text = jsXHR.responseText;
        this.type = jsXHR.responseType;
        this.status = jsXHR.status;
        this.statusText = jsXHR.statusText;
    }
    }

    function dataFromJSXHR(jsXHR: XMLHttpRequest): Data {
    return new Data(jsXHR);
    }

    export function sendCommand(method: string, url: string, headers: Array<Header> | null, data: string | any = ""): Promise<Data> {
        return new Promise<Data>(function (resolve, reject) {
            var jsXHR = new XMLHttpRequest();
            jsXHR.open(method, url);
            const contentType = headers && headers.find(it=>it.header==='Content-Type');
            if (headers != null) {
                headers.forEach(header => {
                jsXHR.setRequestHeader(header.header, header.data)})
            }

            jsXHR.onload = (ev) => {
                if (jsXHR.status < 200 || jsXHR.status >= 300) {
                    reject(dataFromJSXHR(jsXHR));
                }
                resolve(dataFromJSXHR(jsXHR));
            }
            jsXHR.onerror = (ev) => {
                reject('Error ' + method.toUpperCase() + 'ing data to url "');
            };

            if (method === 'POST' || method === 'PUT') {
                if(!contentType){
                    jsXHR.setRequestHeader("Content-Type", "application/json");
                }
                jsXHR.send(JSON.stringify(data));
            }else {
                jsXHR.send();
            }
        });
    }

    /*export function get(url: string, headers: Array<Header> = null): Promise<Data> {
        return sendCommand('GET', url, headers);
    }

    export function post(url: string, data: string = "", headers: Array<Header> = null): Promise<Data> {
        return sendCommand('POST', url, headers, data);
    }

    export function put(url: string, data: string = "", headers: Array<Header> = null): Promise<Data> {
        return sendCommand('PUT', url, headers, data);
    }

    export function del(url:string, headers:Array<Header> = null):Promise < Data > {
        return sendCommand('DELETE', url, headers);
    }*/
}