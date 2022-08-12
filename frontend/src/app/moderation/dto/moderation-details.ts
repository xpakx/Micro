import { UserMin } from "src/app/mention/dto/user-min";
import { EntityWithIdAndContent } from "./entity-with-id-and-content";

export interface ModerationDetails {
    id: number;
    moderated: boolean;
    deleted: boolean;
    createdAt: Date;
    moderatedAt: Date;
    reason: String;
    post: EntityWithIdAndContent;
    comment: EntityWithIdAndContent;
    author: UserMin;
    moderator: UserMin;
}