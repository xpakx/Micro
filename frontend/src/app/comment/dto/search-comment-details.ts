import { PostWithOnlyId } from "src/app/mention/dto/post-with-only-id";
import { CommentAuthor } from "./comment-author";

export interface SearchCommentDetails {
    id: number,
    content: String,
    createdAt: Date,
    edited: boolean,
    deletedByUser: boolean,
    deletedByPostAuthor: boolean,
    user: CommentAuthor,
    likeCount: number,
    dislikeCount: number,
    attachmentUrl: String,
    post: PostWithOnlyId
}