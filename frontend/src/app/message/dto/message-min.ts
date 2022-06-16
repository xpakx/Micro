import { UserMin } from "./user-min";

export interface MessageMin {
    id: number;
    sender: UserMin;
    read: boolean;
    createdAt: Date;
}