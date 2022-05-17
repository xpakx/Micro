import { CommentDetails } from "src/app/comment/dto/comment-details";
import { Page } from "src/app/common/dto/page";
import { PostDetails } from "./post-details";

export interface PostWithComments {
    post: PostDetails;
    comments: Page<CommentDetails>;
}