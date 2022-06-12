import { PostWithOnlyId } from "./post-with-only-id";
import { UserMin } from "./user-min";


export interface MentionDetails {
    id: number;
    author: UserMin;
    mentioned: UserMin;
    post: PostWithOnlyId;
}