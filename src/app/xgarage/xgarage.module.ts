import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule, HashLocationStrategy, LocationStrategy } from '@angular/common';
import { XgarageRoutingModule } from './xgarage-routing.module';
import { DialogService } from 'primeng/dynamicdialog';
import { AccordionModule } from 'primeng/accordion';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { AvatarModule } from 'primeng/avatar';
import { AvatarGroupModule } from 'primeng/avatargroup';
import { BadgeModule } from 'primeng/badge';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CardModule } from 'primeng/card';
import { CarouselModule } from 'primeng/carousel';
import { CascadeSelectModule } from 'primeng/cascadeselect';
import { ChartModule } from 'primeng/chart';
import { CheckboxModule } from 'primeng/checkbox';
import { ChipModule } from 'primeng/chip';
import { ChipsModule } from 'primeng/chips';
// import { CodeHighlighterModule } from 'primeng/codehighlighter';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { ColorPickerModule } from 'primeng/colorpicker';
import { ContextMenuModule } from 'primeng/contextmenu';
import { DataViewModule } from 'primeng/dataview';
import { DialogModule } from 'primeng/dialog';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { FieldsetModule } from 'primeng/fieldset';
import { FileUploadModule } from 'primeng/fileupload';
// import { FullCalendarModule } from '@fullcalendar/angular';
import { GalleriaModule } from 'primeng/galleria';
import { ImageModule } from 'primeng/image';
import { InplaceModule } from 'primeng/inplace';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputMaskModule } from 'primeng/inputmask';
import { InputSwitchModule } from 'primeng/inputswitch';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { KnobModule } from 'primeng/knob';
// import { LightboxModule } from 'primeng/lightbox';
import { ListboxModule } from 'primeng/listbox';
import { MegaMenuModule } from 'primeng/megamenu';
import { MenuModule } from 'primeng/menu';
import { MenubarModule } from 'primeng/menubar';
import { MessagesModule } from 'primeng/messages';
import { MessageModule } from 'primeng/message';
import { MultiSelectModule } from 'primeng/multiselect';
import { OrderListModule } from 'primeng/orderlist';
import { OrganizationChartModule } from 'primeng/organizationchart';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { PaginatorModule } from 'primeng/paginator';
import { PanelModule } from 'primeng/panel';
import { PanelMenuModule } from 'primeng/panelmenu';
import { PasswordModule } from 'primeng/password';
import { PickListModule } from 'primeng/picklist';
import { ProgressBarModule } from 'primeng/progressbar';
import { RadioButtonModule } from 'primeng/radiobutton';
import { RatingModule } from 'primeng/rating';
import { RippleModule } from 'primeng/ripple';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { ScrollTopModule } from 'primeng/scrolltop';
import { SelectButtonModule } from 'primeng/selectbutton';
import { SidebarModule } from 'primeng/sidebar';
import { SkeletonModule } from 'primeng/skeleton';
import { SlideMenuModule } from 'primeng/slidemenu';
import { SliderModule } from 'primeng/slider';
import { SplitButtonModule } from 'primeng/splitbutton';
import { SplitterModule } from 'primeng/splitter';
import { StepsModule } from 'primeng/steps';
import { TabMenuModule } from 'primeng/tabmenu';
import { TableModule } from 'primeng/table';
import { TabViewModule } from 'primeng/tabview';
import { TagModule } from 'primeng/tag';
import { TerminalModule } from 'primeng/terminal';
import { TieredMenuModule } from 'primeng/tieredmenu';
import { TimelineModule } from 'primeng/timeline';
import { ToastModule } from 'primeng/toast';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { ToolbarModule } from 'primeng/toolbar';
import { TooltipModule } from 'primeng/tooltip';
import { TreeModule } from 'primeng/tree';
import { TreeTableModule } from 'primeng/treetable';
import { VirtualScrollerModule } from 'primeng/virtualscroller';
import { AppCodeModule } from '../app.code.component';
import { AppComponent } from '../app.component';
import { AppMainComponent } from '../app.main.component';
import { AppConfigComponent } from '../app.config.component';
import { AppMenuComponent } from '../app.menu.component';
import { AppMenuitemComponent } from '../app.menuitem.component';
import { AppInlineMenuComponent } from '../app.inlinemenu.component';
import { AppRightMenuComponent } from '../app.rightmenu.component';
import { AppBreadcrumbComponent } from '../app.breadcrumb.component';
import { AppTopBarComponent } from '../app.topbar.component';
import { AppFooterComponent } from '../app.footer.component';
import { ClaimComponent } from '../xgarage/core/component/claim/claim.component';
import { TranslateModule } from '@ngx-translate/core';
import { BlockUIModule } from 'primeng/blockui';
import { UsersComponent } from '../xgarage/dashboard/component/users/users.component';
import { UserRolesComponent } from '../xgarage/dashboard/component/user-roles/user-roles.component';
import { UserMainMenuComponent } from '../xgarage/dashboard/component/user-main-menu/user-main-menu.component';
import { UserSubMenuComponent } from '../xgarage/dashboard/component/user-sub-menu/user-sub-menu.component';
import { PermissionsComponent } from '../xgarage/dashboard/component/persmissions/permissions.component';
import { ResetPasswordComponent } from '../xgarage/dashboard/component/changepassword/changepassword.component';
import { RolesComponent } from '../xgarage/dashboard/component/roles/roles.component';
import { RolePermissionsComponent } from '../xgarage/dashboard/component/role-permissions/role-permissions.component';
import { TenantComponent } from '../xgarage/common/component/tenant/tenantcomponent';
import { SuppliersComponent } from '../xgarage/core/component/supplier/suppliers.component';
import { JobComponent } from '../xgarage/core/component/job/job.component';
import { TenantTypeComponent } from '../xgarage/common/component/tenanttype/tenanttype.component';
import { NewJobComponent } from '../xgarage/core/component/job/newjob/newjob.component';
import { RequestComponent } from '../xgarage/core/component/request/request.component';
import { CarComponent } from '../xgarage/core/component/car/car.component';
import { NewCarComponent } from '../xgarage/core/component/car/new-car/new-car.component';
import { PartComponent } from '../xgarage/core/component/part/part.component';
import { NewPartComponent } from '../xgarage/core/component/part/new-part/new-part.component';
import { JobDetailsComponent } from '../xgarage/core/component/job/job-details/jobdetails.component';
import { NewRequestComponent } from '../xgarage/core/component/request/new-request/new-request.component';
import { RequestDetailsComponent } from '../xgarage/core/component/request/request-details/requestdetails.component';
import { BrandComponent } from '../xgarage/common/component/brand/brand.component';
import { CarModelComponent } from '../xgarage/common/component/car-model/car-model.component';
import { CategoryComponent } from '../xgarage/common/component/category/category.component';
import { SubCategoryComponent } from '../xgarage/common/component/sub-category/sub-category.component';
import { NewBidComponent } from '../xgarage/core/component/bid/new-bid/new-bid.component';
import { BidDetailsComponent } from '../xgarage/core/component/bid/bid-details/bid-details.component';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { SupplierDashbaordComponent } from '../xgarage/dashboard/component/supplier-dashbaord/supplier-dashbaord.component';
import { SupplierprofileComponent } from '../xgarage/common/component/supplierprofile/supplierprofile.component';
import { OrderComponent } from '../xgarage/core/component/order/order.component';
import { OrderDetailsComponent } from '../xgarage/core/component/order/order-details/orderdetails.component';
import { GoogleMapsModule } from '@angular/google-maps';
import { NumToWordsPipe } from '../xgarage/common/pipes/num-to-words.pipe';
import { XgarageComponent } from './xgarage.component';
import { AuthGuard } from '../auth/guards/auth.guard';
import { AuthService } from '../auth/services/auth.service';
import { RandomGuard } from '../auth/guards/random.guard';
import { TokenInterceptor } from '../auth/token.interceptor';
import { AddClaimComponent } from './core/component/claim/add-claim/add-claim.component';
import { DetailsCardComponent } from './common/component/details-card/details-card.component';
import { ClaimDetailsComponent } from './core/component/claim/claim-details/claim-details.component';
import { PrivacyComponent } from './common/component/privacy/privacy.component';
import { EditClaimComponent } from './core/component/claim/edit-claim/edit-claim.component';
import { NewClaimComponent } from './core/component/claim/new-claim/new-claim.component';
import { ClaimOrderComponent } from './core/component/claim/claim-order/claim-order.component';
import { ClaimOrderDetailsComponent } from './core/component/claim/claim-order/claim-order-details/claim-order-details.component';
import { PrintComponent } from './core/component/claim/print/print.component';

@NgModule({
    declarations: [
        XgarageComponent,
        OrderComponent,
        OrderDetailsComponent,
        AppComponent,
        AppMainComponent,
        AppConfigComponent,
        AppMenuComponent,
        AppMenuitemComponent,
        AppInlineMenuComponent,
        AppRightMenuComponent,
        AppBreadcrumbComponent,
        AppTopBarComponent,
        AppFooterComponent,
        UsersComponent,
        UserRolesComponent,
        UserMainMenuComponent,
        UserSubMenuComponent,
        ResetPasswordComponent,
        RolesComponent,
        RolePermissionsComponent,
        TenantComponent,
        TenantTypeComponent,
        PermissionsComponent,
        SuppliersComponent,
        ClaimComponent,
        JobComponent,
        NewJobComponent,
        RequestComponent,
        CarComponent,
        NewCarComponent,
        PartComponent,
        NewPartComponent,
        JobDetailsComponent,
        NewRequestComponent,
        RequestDetailsComponent,
        BrandComponent,
        CarModelComponent,
        CategoryComponent,
        SubCategoryComponent,
        NewBidComponent,
        BidDetailsComponent,
        SupplierDashbaordComponent,
        SupplierprofileComponent,
        NumToWordsPipe,
        AddClaimComponent,
        DetailsCardComponent,
        ClaimDetailsComponent,
        PrivacyComponent,
        EditClaimComponent,
        NewClaimComponent,
        ClaimOrderComponent,
        ClaimOrderDetailsComponent,
        PrintComponent
    ],
    imports: [
        CommonModule,
        XgarageRoutingModule,
        GoogleMapsModule,
        ProgressSpinnerModule,
        BlockUIModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        AccordionModule,
        AutoCompleteModule,
        AvatarModule,
        AvatarGroupModule,
        BadgeModule,
        BreadcrumbModule,
        ButtonModule,
        CalendarModule,
        CardModule,
        CarouselModule,
        CascadeSelectModule,
        ChartModule,
        CheckboxModule,
        ChipModule,
        ChipsModule,
        // CodeHighlighterModule,
        ConfirmDialogModule,
        ConfirmPopupModule,
        ColorPickerModule,
        ContextMenuModule,
        DataViewModule,
        DialogModule,
        DividerModule,
        DropdownModule,
        FieldsetModule,
        FileUploadModule,
        GalleriaModule,
        ImageModule,
        InplaceModule,
        InputNumberModule,
        InputMaskModule,
        InputSwitchModule,
        InputTextModule,
        InputTextareaModule,
        KnobModule,
        // LightboxModule,
        ListboxModule,
        MegaMenuModule,
        MenuModule,
        MenubarModule,
        MessageModule,
        MessagesModule,
        MultiSelectModule,
        OrderListModule,
        OrganizationChartModule,
        OverlayPanelModule,
        PaginatorModule,
        PanelModule,
        PanelMenuModule,
        PasswordModule,
        PickListModule,
        ProgressBarModule,
        RadioButtonModule,
        RatingModule,
        RippleModule,
        ScrollPanelModule,
        ScrollTopModule,
        SelectButtonModule,
        SidebarModule,
        SkeletonModule,
        SlideMenuModule,
        SliderModule,
        SplitButtonModule,
        SplitterModule,
        StepsModule,
        TableModule,
        TabMenuModule,
        TabViewModule,
        TagModule,
        TerminalModule,
        TimelineModule,
        TieredMenuModule,
        ToastModule,
        ToggleButtonModule,
        ToolbarModule,
        TooltipModule,
        TreeModule,
        TreeTableModule,
        VirtualScrollerModule,
        AppCodeModule,
        TranslateModule,
    ],
    providers: [
        AuthGuard,
        AuthService,
        RandomGuard,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true
        },
        { provide: LocationStrategy, useClass: HashLocationStrategy }, DialogService
    ]
})
export class XgarageModule { }
