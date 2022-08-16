import { PostWithOnlyId } from "src/app/mention/dto/post-with-only-id";
import { EntityWithIdAndContent } from "./entity-with-id-and-content";

export interface CommentWithIdAndContent extends EntityWithIdAndContent {
    post: PostWithOnlyId;
}