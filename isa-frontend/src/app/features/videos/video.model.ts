export interface VideoPost {
  id: number;
  title: string;
  description: string;
  tags: string[];
  location?: string;
  createdAt: string;
  ownerId?: number;
  ownerFullName?: string;
  views: number;
  likesCount: number;
  isLikedByMe: boolean;
}
