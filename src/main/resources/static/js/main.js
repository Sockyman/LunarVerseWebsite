let ws;
let terminal;
let command = '';
let minColumn = 0;
let acceptInput = true;

function onload() {
    var baseTheme = {
        foreground: '#F8F8F8',
        background: '#2D2E2C',
        selection: '#5DA5D533',
        black: '#1E1E1D',
        brightBlack: '#262625',
        red: '#CE5C5C',
        brightRed: '#FF7272',
        green: '#5BCC5B',
        brightGreen: '#72FF72',
        yellow: '#CCCC5B',
        brightYellow: '#FFFF72',
        blue: '#5D5DD3',
        brightBlue: '#7279FF',
        magenta: '#BC5ED1',
        brightMagenta: '#E572FF',
        cyan: '#5DA5D5',
        brightCyan: '#72F0FF',
        white: '#F8F8F8',
        brightWhite: '#FFFFFF'
    };

    terminal = new Terminal({
        fontFamily: '"Cascadia Code", Menlo, monospace',
        theme: baseTheme
    });


    ws = new WebSocket("ws://localhost:8080/ws");
    ws.onopen = (event) => {
        terminal.writeln("[SERVER] connected")
        console.log("Connected to websocket");
    };

    ws.onclose = (event) => {
        terminal.writeln("[SERVER] disconnected");
        console.log("Websocket closed");
    };

    terminal.open(document.getElementById("terminal"));
    terminal.writeln("[SERVER] trying to connect");

    terminal.onData(key => {
        if (!acceptInput) {
            return;
        }

        switch (key) {
            case '\u007f':
                if (terminal._core.buffer.x > minColumn) {
                    terminal.write('\b \b');
                    if (command.length > 0) {
                        command = command.slice(0, command.length - 1);
                    }
                }
                break;
            case '\r':
                terminal.writeln('');
                command += '\r\n';
                sendMessage('input', {'message': command});
                command = '';
                break;
            default:
                if (key >= '\u0020' && key <= '\u007e') {
                    terminal.write(key);
                    command += key;
                }
                break;
        }
        
    });

    ws.onmessage = (event) => {
        handleMessage(event.data);
    };
}

function handleMessage(message) {
    let data = JSON.parse(message);
    console.log(data);
    switch (data.type) {
        case 'print':
            terminal.write(data.message);
            break;
        case 'acceptInput':
            acceptInput = true;
            break;
        default:
            console.log(`Invalid message type ${data.type}`);
            break;
    }
}

function sendMessage(type, value) {
    value.type = type;
    ws.send(JSON.stringify(value));
}
