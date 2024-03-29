import { PostAuthor } from "./post-author";

export interface PostDetails {
    id: number,
    content: String,
    createdAt: Date,
    edited: boolean,
    user: PostAuthor,
    likeCount: number,
    dislikeCount: number,
    attachmentUrl: String
}