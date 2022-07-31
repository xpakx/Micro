import { CommentWithData } from "src/app/comment/dto/comment-with-data";
import { Page } from "src/app/common/dto/page";
import { PostDetails } from "./post-details";

export interface PostWithComments {
    post: PostDetails;
    comments: Page<CommentWithData>;
    liked: boolean;
    disliked: boolean;
    fav: boolean;
}