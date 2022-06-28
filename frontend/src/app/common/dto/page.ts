import { Pageable } from "./pageable";

export interface Page<T> {
    content: T[];
    pageable: Pageable;
    totalPages: number;
    last: boolean;
    number: number;
    sort: {
        sorted: boolean;
        unsorted: boolean;
        empty: boolean;
    };
    size: number;
    numberOfElements: number;
    totalElements: number;
    first: boolean;
    empty: boolean;
}
