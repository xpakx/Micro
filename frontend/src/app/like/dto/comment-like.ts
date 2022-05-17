export interface CommentLike {
    id: number;
    commentId: number;
    voted: boolean;
    positive: boolean;
    totalLikes: number;
}
