import { UserMin } from "./user-min";

export interface MessageDetails {
    id: number;
    sender: UserMin;
    read: boolean;
    content: string;
    createdAt: Date;
}