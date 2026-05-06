import { WebSocket, WebSocketServer } from 'ws';
import { IncomingMessage } from 'http';

const PORT = 8080;
const wss = new WebSocketServer({ port: PORT });

// Extend WebSocket type to include isAlive
interface ExtendedWebSocket extends WebSocket {
  isAlive: boolean;
}

const clients = new Map<string, ExtendedWebSocket>();

// Heartbeat: Responds to pings to keep the connection alive.
function heartbeat(this: ExtendedWebSocket) {
  this.isAlive = true;
}

wss.on('connection', (ws: ExtendedWebSocket, req: IncomingMessage) => {
  // Use URL API for safer parsing of the UID
  const host = req.headers.host || 'localhost';
  const url = new URL(req.url || '', `http://${host}`);
  const uid = (req.headers['uid'] as string) || url.searchParams.get('uid');

  if (!uid) {
    console.log('Connection rejected: Missing UID');
    ws.terminate();
    return;
  }

  // Clean up duplicate connections for the same UID
  const existingWs = clients.get(uid);
  if (existingWs) {
    console.log(`Duplicate UID ${uid}: Terminating old connection.`);
    existingWs.terminate();
  }

  // Setup connection state
  ws.isAlive = true;
  clients.set(uid, ws);

  console.log(`Device connected: ${uid} (Total: ${clients.size})`);

  // Event listeners
  ws.on('pong', heartbeat);

  ws.on('message', (message: Buffer) => {
    try {
      const data = JSON.parse(message.toString());
      const to = data.to;

      if (!to) return;

      console.log(`Routing message from ${uid} to ${to}`);

      const targetWs = clients.get(to);
      if (targetWs && targetWs.readyState === WebSocket.OPEN) {
        targetWs.send(message.toString());
      } else {
        console.log(`Target ${to} not found or disconnected.`);
      }
    } catch(e: any) {
      console.error(`Invalid message from ${uid}:`, e.message);
    }
  });

  ws.on('error', (err: Error) => {
    console.error(`Socket error for ${uid}:`, err);
  });

  ws.on('close', () => {
    // Only delete from map if this specific socket is the one registered
    if (clients.get(uid) === ws) {
      clients.delete(uid);
      console.log(`Device disconnected: ${uid} (Total: ${clients.size})`);
    }
  });
});

// Heartbeat Interval: Check every 30s if connections are still alive
const interval = setInterval(() => {
  wss.clients.forEach((client) => {
    const ws = client as ExtendedWebSocket;

    if (ws.isAlive === false) {
      console.log('Terminating dead connection.');
      return ws.terminate();
    }

    ws.isAlive = false;
    ws.ping();
  });
}, 30000);

wss.on('close', () => {
  clearInterval(interval);
});

console.log(`Local WebRTC Signaling Server running on ws://0.0.0.0:${PORT}`);
