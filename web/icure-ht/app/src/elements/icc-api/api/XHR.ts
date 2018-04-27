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
    }

    function dataFromJSXHR(jsXHR: XMLHttpRequest): Data {
        var data = new Data();
        data.headers = jsXHR.getAllResponseHeaders();
        data.body = jsXHR.response;
        data.text = jsXHR.responseText;
        data.type = jsXHR.responseType;
        data.status = jsXHR.status;
        data.statusText = jsXHR.statusText;
        return data;
    }

    export function sendCommand(method: string, url: string, headers: Array<Header> | null, data: string | any = ""): Promise<Data> {
        return new Promise<Data>(function (resolve, reject) {
            var jsXHR = new XMLHttpRequest();
            jsXHR.open(method, url);

            if (headers != null)
                headers.forEach(header =>
                    jsXHR.setRequestHeader(header.header, header.data));

            jsXHR.onload = (ev) => {
                if (jsXHR.status < 200 || jsXHR.status >= 300) {
                    reject(dataFromJSXHR(jsXHR));
                }
                resolve(dataFromJSXHR(jsXHR));
            }
            jsXHR.onerror = (ev) => {
                reject('Error ' + method.toUpperCase() + 'ing data to url "');
            };

            if (method == 'POST')
                jsXHR.send(data);
            else
                jsXHR.send();
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