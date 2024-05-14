import { Supplier } from "../model/supplier";

export interface UpdateJobDto{
    id?: number;
    jobNumber?: string;
    status?: number;
    privacy?: string;
    supplierList?: Supplier[];
}
