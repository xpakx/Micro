import { CommentDetails } from "./comment-details";

export interface CommentWithData {
    comment: CommentDetails;
    liked: boolean;
    disliked: boolean;
}