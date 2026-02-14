export interface VideoPremiereResponse {
  available: boolean;
  mode: 'WAIT' | 'HLS' | 'MP4';
  url: string | null;
  offsetSeconds: number;
  scheduledAt: string | null; // ISO string iz backenda
  serverNow: string;          // ISO string
}