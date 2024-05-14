import { GenericEntity } from "../../common/generic/genericentity";
import { SubCategory } from "../../common/model/subcategory";

export interface Part extends GenericEntity{
    id?: number;
    name?: string;
    status?: number;
    subCategory?: SubCategory;
    subCategoryId?: number;
    categoryId?: number;
}
