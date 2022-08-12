export interface ModerationDetails {
    id: number;
    moderated: boolean;
    deleted: boolean;
    createdAt: Date;
    moderatedAt: Date;
    reason: String;
}