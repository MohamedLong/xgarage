import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styles: [`
    :host ::ng-deep .p-password input {
    width: 100%;
    padding:1rem;
    }

    :host ::ng-deep .pi-eye{
      transform:scale(1.6);
      margin-right: 1rem;
      color: var(--primary-color) !important;
    }

    :host ::ng-deep .pi-eye-slash{
      transform:scale(1.6);
      margin-right: 1rem;
      color: var(--primary-color) !important;
    }

    .pages-body .topbar {background-color: #fff;}
  `],
    providers: [MessageService]
})
export class LoginComponent implements OnInit {

    loginForm: UntypedFormGroup;
    decodedToken: string;
    isLoading: boolean = false;
    destination: string;
    showPassword: boolean = false;

    constructor(private authService: AuthService, private formBuilder: UntypedFormBuilder,
        private route: ActivatedRoute,
        private router: Router, private messageService: MessageService) { }

    ngOnInit() {
        this.route.queryParams.subscribe(params => {
            this.destination = params['destination'];
        });

        this.loginForm = this.formBuilder.group({
            username: [''],
            password: ['']
        });
    }


    get f() { return this.loginForm.controls; }

    login() {
        this.isLoading = !this.isLoading;
        this.authService.login(
            {
                username: this.f.username.value,
                password: this.f.password.value
            }
        ).subscribe(
            {
                next: (success) => {
                    // console.log('success login')
                    if (this.authService.isLoggedIn()) {
                        // console.log('user is' + this.authService.isLoggedIn() )
                        this.authService.doStoreUser(this.authService.getJwtToken(), this.router, this.destination);
                    }
                },
                error: (e) => {
                    console.log(e)
                    this.isLoading = false;
                    if(e.type) {
                        this.messageService.add({ severity: 'error', summary: 'Erorr', detail: 'Something Went Wrong, PLease Try Again Later.' });
                    } else {
                        this.messageService.add({ severity: 'error', summary: 'Erorr', detail: e });
                    }

                }
            }
        );
    }

}
