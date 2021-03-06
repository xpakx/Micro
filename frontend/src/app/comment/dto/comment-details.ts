import { CommentAuthor } from "./comment-author";

export interface CommentDetails {
    id: number,
    content: String,
    createdAt: Date,
    edited: boolean,
    deletedByUser: boolean,
    deletedByPostAuthor: boolean,
    user: CommentAuthor,
    likeCount: number,
    dislikeCount: number
}