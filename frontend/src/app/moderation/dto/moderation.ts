export interface Moderation {
    id: number;
    moderated: boolean;
    deleted: boolean;
    createdAt: Date;
    moderatedAt: Date;
    reason: String;
}