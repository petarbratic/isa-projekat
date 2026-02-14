import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from './auth.service';

export interface StreamingChatMessage {
  authorName: string;
  text: string;
  timestamp: number;
}

const WS_BASE = 'http://localhost:8081';

@Injectable({
  providedIn: 'root'
})
export class StreamingChatService {

  private client: Client | null = null;
  private currentVideoId: number | null = null;
  private readonly messages$ = new Subject<StreamingChatMessage>();

  constructor(private authService: AuthService) {}

  get messages() {
    return this.messages$.asObservable();
  }

  connect(videoId: number): void {
    if (this.currentVideoId === videoId && this.client?.active) {
      return;
    }
    this.disconnect();

    this.currentVideoId = videoId;
    const wsUrl = `${WS_BASE}/ws`;

    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl) as any,
      reconnectDelay: 3000,
      onConnect: () => {
        if (!this.client || this.currentVideoId === null) return;
        this.client.subscribe(`/topic/video/${this.currentVideoId}`, (msg) => {
          try {
            const body = JSON.parse(msg.body) as StreamingChatMessage;
            this.messages$.next(body);
          } catch {
            // ignore invalid messages
          }
        });
      },
      onStompError: () => {},
    });

    this.client.activate();
  }

  disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
    this.currentVideoId = null;
  }

  sendMessage(videoId: number, text: string): void {
    const token = this.authService.getToken();
    if (!token || !text?.trim()) return;

    if (!this.client?.active) {
      return;
    }

    this.client.publish({
      destination: `/app/video/${videoId}/chat`,
      body: JSON.stringify({ text: text.trim(), token }),
    });
  }

  isConnected(): boolean {
    return !!this.client?.active;
  }
}
