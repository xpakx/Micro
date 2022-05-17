export interface PostLike {
    id: number;
    postId: number;
    voted: boolean;
    positive: boolean;
    totalLikes: number;
}