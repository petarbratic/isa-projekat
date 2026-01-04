export interface CommentResponse {
  id: number;
  authorEmail: string;
  authorName: string;
  text: string;
  createdAt: string; 
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; 
  size: number;
}