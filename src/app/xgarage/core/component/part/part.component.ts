import { Part } from './../../model/part';
import { SubCategory } from './../../model/subcategory';
import { Category } from './../../model/category';
import { SubCategoryService } from './../../service/subcategory.service';
import { PartService } from './../../service/part.service';
import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { GenericComponent } from 'src/app/xgarage/common/generic/genericcomponent';
import { CategoryService } from '../../service/category.service';

@Component({
    selector: 'app-part',
    templateUrl: './part.component.html',
    styleUrls: ['../../../../demo/view/tabledemo.scss'],

    providers: [MessageService, ConfirmationService, DatePipe]
})
export class PartComponent extends GenericComponent implements OnInit {

    constructor(public route: ActivatedRoute,
        public messageService: MessageService, public datePipe: DatePipe, private partService: PartService,
        private categoryService: CategoryService, private subCategoryService: SubCategoryService,
        breadcrumbService: AppBreadcrumbService) {
        super(route, datePipe, breadcrumbService);
    }

    category: Category;
    categories: Category[];
    subCategory: SubCategory = {};
    subCategories: SubCategory[] = [];
    selectedPart: Part = {};
    enabled: boolean = false;
    selectedCategory: Category;
    selectedSubCategory: SubCategory;
    selectedSubCategries: SubCategory[] = [];
    partName: string = "";
    part: Part = {};

    ngOnInit(): void {
        this.getAll();

        this.breadcrumbService.setItems([{ 'label': 'Parts', routerLink: ['parts'] }]);
    }

    getAll() {
        this.getAllCategories();
        this.getAllSubCategories();
        this.partService.getAll().subscribe({
            next: (parts) => {
                this.masters = parts.map(part => {
                    this.category = this.categories.find(c => c.id === part.categoryId);
                    this.subCategory = this.subCategories.find(sc => sc.id === part.subCategoryId);
                    return {
                        ...part,
                        categoryName: this.category?.name,
                        subCategoryName: this.subCategory?.name,
                        statusText: this.getStatusText(part.status)
                    };
                });
                this.cols = [
                    { field: 'id', header: 'id' },
                    { field: 'name', header: 'Part Name' },
                    { field: 'statusText', header: 'Status' },
                    { field: 'subCategoryName', header: 'Sub-Category' },
                    { field: 'categoryName', header: 'Category' }
                ];
                this.loading = false;
            },
            error: (e) => this.messageService.add({ severity: 'error', summary: 'Server Error', detail: e.error, life: 3000 })
        });
    }

    getAllCategories() {
        this.categoryService.getAll().subscribe({
            next: (data) => {
                this.categories = data;
            },
            error: (e) => alert(e)
        })
    }

    getAllSubCategories() {
        this.subCategoryService.getAll().subscribe({
            next: (data) => {
                this.subCategories = data;
                this.selectedSubCategries = data;
            },
            error: (e) => alert(e)
        })
    }

    changeStatus(id: number) {
        if (id != null) {
            this.partService.update(this.selectedPart).subscribe(
                {
                    next: (data) => {
                        this.master = data;
                        this.getAll();
                        this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Part Status Changed', life: 3000 });
                    },
                    error: (e) => {
                        this.messageService.add({
                            severity: 'error', summary: 'Error',
                            detail: e.error.message
                        })
                    }
                })
        }
    }

    approvePart(id: number) {
        this.selectedPart = this.masters.find(val => val.id == id);
        this.selectedPart.status = 1;
        this.changeStatus(id);
    }

    rejectPart(id: number) {
        this.selectedPart = this.masters.find(val => val.id == id);
        this.selectedPart.status = -1;
        this.changeStatus(id);
    }

    getStatusText(status: number): string {
        switch (status) {
            case 0:
                return 'Pending';
            case 1:
                return 'Approved';
            case -1:
                return 'Rejected';
            default:
                return '';
        }
    }

    onCategoryChange(id: number) {
        console.log(id)
        this.subCategoryService.getSubCategoriesByCategory(id).subscribe(res => {
            this.selectedSubCategries = res;
        }, err => console.log(err));

        console.log(this.selectedSubCategries)
    }

    // getSubcategoryName(partSubcategoryId): string {
    //     let subcategory = this.subCategories.filter(subcat => {
    //         return subcat.id == partSubcategoryId;
    //     });

    //     return subcategory[0].name;
    // }

    edit(part) {
        //console.log({ part })
        this.selectedSubCategory = this.subCategories.find(sub => { return sub.id == part.subCategoryId });
        this.selectedCategory = this.categories.find(cat => { return cat.id == part.categoryId });
        this.partName = part.name;


        this.part = {
            id: part.id,
            name: this.partName,
            subCategory: this.selectedSubCategory,
            subCategoryId: this.selectedSubCategory.id,
            categoryId: this.selectedCategory.id
        };

        this.masterDialog = true;
    }

    save() {
        //console.log(this.selectedCategory, this.selectedSubCategory, this.partName);
        this.submitted = true;
        if (this.part.id) {
            this.part.name = this.partName;
            //console.log('update part', this.part)
            this.partService.update(this.part).subscribe(res => {
                this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'part upadated successfully', life: 3000 });
                this.part = {};
            }, err => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error.message })
            })


            this.masterDialog = false;

        } else {
            //console.log('add part')
            this.part = {
                name: this.partName,
                subCategory: this.selectedSubCategory,
                subCategoryId: this.selectedSubCategory.id,
                categoryId: this.selectedCategory.id
            };

            if (this.selectedCategory && this.selectedSubCategory && this.partName) {
                this.partService.add(this.part).subscribe(res => {
                    this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'part added successfully', life: 3000 });
                    this.part = {};
                }, err => {
                    this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error.message })
                })


                this.masterDialog = false;
            }
        }

    }

}
