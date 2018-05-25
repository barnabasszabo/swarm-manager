import { Component, Input, OnInit, AfterViewInit, OnDestroy, ElementRef, Renderer, ViewChild } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Location } from '@angular/common';
import { Router, RoutesRecognized } from '@angular/router';
import { MenuItem, ScrollPanel } from 'primeng/primeng';
import { AppComponent } from '../app.component';
import { SwarmService } from '../swarm/swarm.service';

import * as _ from 'lodash';
import { Pool } from '../swarm/model/Pool';
import 'rxjs/add/operator/pairwise';

@Component({
  selector: 'app-menu',
  templateUrl: './app.menu.component.html'
})
export class AppMenuComponent implements OnInit, OnDestroy, AfterViewInit {

  @Input() reset: boolean;

  model: any[];

  private poolInterval;
  private poolCache: Pool[];

  @ViewChild('scrollPanel') layoutMenuScrollerViewChild: ScrollPanel;

  constructor(public app: AppComponent, private swarmservice: SwarmService, private router: Router) {
    if ( localStorage.getItem('prevUrl') ) {
      this.router.navigateByUrl(localStorage.getItem('prevUrl'));
    }
    this.router.events
      .subscribe((event) => {
        if (event instanceof RoutesRecognized) {
          localStorage.setItem('prevUrl', event.urlAfterRedirects);
        }
      });
  }

  ngAfterViewInit() {
    setTimeout(() => { this.layoutMenuScrollerViewChild.moveBar(); }, 100);
  }

  ngOnInit() {
    this.poolInterval = setInterval(() => {
      this.refresh();
    }, 30000);
    this.refresh();
  }
  ngOnDestroy() {
    if (this.poolInterval) {
      clearInterval(this.poolInterval);
    }
  }

  refresh() {
    console.log('Refreshing pool list in sidebar');

    this.swarmservice.getPools().subscribe(data => {
      if (!_.isEqual(this.poolCache, data)) {
        console.log('Pool changed from the last check');

        this.poolCache = data;
        const items = [
          { label: 'All ecosystem', icon: 'subject', routerLink: ['/pool/all'] }
        ];
        this.poolCache.forEach(pool => {
          items.push({ label: pool.displayName, icon: 'subject', routerLink: ['pool/' + pool.id] });
        });

        this.model = [
          { label: 'FAQ', icon: 'fa-info', routerLink: ['/faq'] },
          {
            label: 'Pools', icon: 'menu',
            items: items,
            expanded: true
          },
          {
            label: 'Colors', icon: 'palette',
            items: [
              {
                label: 'Layout Palette', icon: 'palette',
                items: [
                  {
                    label: 'Flat', icon: 'format_paint',
                    items: [
                      // tslint:disable-next-line:max-line-length
                      { label: 'Blue Grey - Green', icon: 'brush', command: (event) => { this.changeLayout('bluegrey'); } },
                      { label: 'Indigo - Pink', icon: 'brush', command: (event) => { this.changeLayout('indigo'); } },
                      { label: 'Pink - Amber', icon: 'brush', command: (event) => { this.changeLayout('pink'); } },
                      // tslint:disable-next-line:max-line-length
                      { label: 'Deep Purple - Orange', icon: 'brush', command: (event) => { this.changeLayout('deeppurple'); } },
                      { label: 'Blue - Amber', icon: 'brush', command: (event) => { this.changeLayout('blue'); } },
                      {
                        label: 'Light Blue - Blue Grey', icon: 'brush',
                        command: (event) => { this.changeLayout('lightblue'); }
                      },
                      { label: 'Cyan - Amber', icon: 'brush', command: (event) => { this.changeLayout('cyan'); } },
                      { label: 'Teal - Red', icon: 'brush', command: (event) => { this.changeLayout('teal'); } },
                      { label: 'Green - Brown', icon: 'brush', command: (event) => { this.changeLayout('green'); } },
                      // tslint:disable-next-line:max-line-length
                      { label: 'Light Green - Purple', icon: 'brush', command: (event) => { this.changeLayout('lightgreen'); } },
                      { label: 'Lime - Blue Grey', icon: 'brush', command: (event) => { this.changeLayout('lime'); } },
                      { label: 'Yellow - Teal', icon: 'brush', command: (event) => { this.changeLayout('yellow'); } },
                      { label: 'Amber - Pink', icon: 'brush', command: (event) => { this.changeLayout('amber'); } },
                      // tslint:disable-next-line:max-line-length
                      { label: 'Orange - Indigo', icon: 'brush', command: (event) => { this.changeLayout('orange'); } },
                      // tslint:disable-next-line:max-line-length
                      { label: 'Deep Orange - Cyan', icon: 'brush', command: (event) => { this.changeLayout('deeporange'); } },
                      { label: 'Brown - Cyan', icon: 'brush', command: (event) => { this.changeLayout('brown'); } },
                      { label: 'Grey - Indigo', icon: 'brush', command: (event) => { this.changeLayout('grey'); } }
                    ]
                  },
                  {
                    label: 'Special', icon: 'format_paint',
                    items: [
                      // tslint:disable-next-line:max-line-length
                      { label: 'Reflection', icon: 'brush', command: (event) => { this.changeLayout('reflection'); } },
                      { label: 'Moody', icon: 'brush', command: (event) => { this.changeLayout('moody'); } },
                      { label: 'Cityscape', icon: 'brush', command: (event) => { this.changeLayout('cityscape'); } },
                      { label: 'Cloudy', icon: 'brush', command: (event) => { this.changeLayout('cloudy'); } },
                      { label: 'Storm', icon: 'brush', command: (event) => { this.changeLayout('storm'); } },
                      { label: 'Palm', icon: 'brush', command: (event) => { this.changeLayout('palm'); } },
                      { label: 'Flatiron', icon: 'brush', command: (event) => { this.changeLayout('flatiron'); } }
                    ]
                  },
                ]
              },
              {
                label: 'Themes', icon: 'brush', badge: '5',
                items: [
                  { label: 'Blue Grey - Green', icon: 'brush', command: (event) => { this.changeTheme('bluegrey'); } },
                  { label: 'Indigo - Pink', icon: 'brush', command: (event) => { this.changeTheme('indigo'); } },
                  { label: 'Pink - Amber', icon: 'brush', command: (event) => { this.changeTheme('pink'); } },
                  { label: 'Purple - Pink', icon: 'brush', command: (event) => { this.changeTheme('purple'); } },
                  // tslint:disable-next-line:max-line-length
                  { label: 'Deep Purple - Orange', icon: 'brush', command: (event) => { this.changeTheme('deeppurple'); } },
                  { label: 'Blue - Amber', icon: 'brush', command: (event) => { this.changeTheme('blue'); } },
                  // tslint:disable-next-line:max-line-length
                  { label: 'Light Blue - Blue Grey', icon: 'brush', command: (event) => { this.changeTheme('lightblue'); } },
                  { label: 'Cyan - Amber', icon: 'brush', command: (event) => { this.changeTheme('cyan'); } },
                  { label: 'Teal - Red', icon: 'brush', command: (event) => { this.changeTheme('teal'); } },
                  { label: 'Green - Brown', icon: 'brush', command: (event) => { this.changeTheme('green'); } },
                  // tslint:disable-next-line:max-line-length
                  { label: 'Light Green - Purple', icon: 'brush', command: (event) => { this.changeTheme('lightgreen'); } },
                  { label: 'Lime - Blue Grey', icon: 'brush', command: (event) => { this.changeTheme('lime'); } },
                  { label: 'Yellow - Teal', icon: 'brush', command: (event) => { this.changeTheme('yellow'); } },
                  { label: 'Amber - Pink', icon: 'brush', command: (event) => { this.changeTheme('amber'); } },
                  { label: 'Orange - Indigo', icon: 'brush', command: (event) => { this.changeTheme('orange'); } },
                  { label: 'Deep Orange - Cyan', icon: 'brush', command: (event) => { this.changeTheme('deeporange'); } },
                  { label: 'Brown - Cyan', icon: 'brush', command: (event) => { this.changeTheme('brown'); } },
                  { label: 'Grey - Indigo', icon: 'brush', command: (event) => { this.changeTheme('grey'); } }
                ]
              }
            ]
          }
        ];
      }
    });
  }

  changeTheme(theme) {
    const themeLink: HTMLLinkElement = <HTMLLinkElement>document.getElementById('theme-css');
    themeLink.href = 'assets/theme/theme-' + theme + '.css';
  }

  changeLayout(theme) {
    const layoutLink: HTMLLinkElement = <HTMLLinkElement>document.getElementById('layout-css');
    layoutLink.href = 'assets/layout/css/layout-' + theme + '.css';
  }
}

@Component({
  /* tslint:disable:component-selector */
  selector: '[app-submenu]',
  /* tslint:enable:component-selector */
  template: `
        <ng-template ngFor let-child let-i="index" [ngForOf]="(root ? item : item.items)">
            <li [ngClass]="{'active-menuitem': isActive(i)}" [class]="child.badgeStyleClass">
                <a [href]="child.url||'#'" (click)="itemClick($event,child,i)" *ngIf="!child.routerLink"
                   [attr.tabindex]="!visible ? '-1' : null" [attr.target]="child.target"
                   (mouseenter)="onMouseEnter(i)" class="ripplelink">
                    <i class="material-icons">{{child.icon}}</i>
                    <span class="menuitem-text">{{child.label}}</span>
                    <i class="material-icons layout-submenu-toggler" *ngIf="child.items">keyboard_arrow_down</i>
                    <span class="menuitem-badge" *ngIf="child.badge">{{child.badge}}</span>
                </a>

                <a (click)="itemClick($event,child,i)" *ngIf="child.routerLink"
                    [routerLink]="child.routerLink" routerLinkActive="active-menuitem-routerlink"
                   [routerLinkActiveOptions]="{exact: true}" [attr.tabindex]="!visible ? '-1' : null" [attr.target]="child.target"
                   (mouseenter)="onMouseEnter(i)" class="ripplelink">
                    <i class="material-icons">{{child.icon}}</i>
                    <span class="menuitem-text">{{child.label}}</span>
                    <i class="material-icons layout-submenu-toggler" *ngIf="child.items">>keyboard_arrow_down</i>
                    <span class="menuitem-badge" *ngIf="child.badge">{{child.badge}}</span>
                </a>
                <ul app-submenu [item]="child" *ngIf="child.items" [visible]="isActive(i)" [reset]="reset"
                    [@children]="(app.isHorizontal())&&root ? isActive(i) ?
                    'visible' : 'hidden' : isActive(i) ? 'visibleAnimated' : 'hiddenAnimated'"></ul>
            </li>
        </ng-template>
    `,
  animations: [
    trigger('children', [
      state('hiddenAnimated', style({
        height: '0px'
      })),
      state('visibleAnimated', style({
        height: '*'
      })),
      state('visible', style({
        height: '*',
        'z-index': 100
      })),
      state('hidden', style({
        height: '0px',
        'z-index': '*'
      })),
      transition('visibleAnimated => hiddenAnimated', animate('400ms cubic-bezier(0.86, 0, 0.07, 1)')),
      transition('hiddenAnimated => visibleAnimated', animate('400ms cubic-bezier(0.86, 0, 0.07, 1)'))
    ])
  ]
})
export class AppSubMenuComponent {

  @Input() item: MenuItem;

  @Input() root: boolean;

  @Input() visible: boolean;

  _reset: boolean;

  activeIndex: number;

  constructor(public app: AppComponent, public router: Router, public location: Location, public appMenu: AppMenuComponent) { }

  itemClick(event: Event, item: MenuItem, index: number) {
    if (this.root) {
      this.app.menuHoverActive = !this.app.menuHoverActive;
      event.preventDefault();
    }

    // avoid processing disabled items
    if (item.disabled) {
      event.preventDefault();
      return true;
    }

    // activate current item and deactivate active sibling if any
    if (item.routerLink || item.items || item.command || item.url) {
      this.activeIndex = (this.activeIndex as number === index) ? -1 : index;
    }

    // execute command
    if (item.command) {
      item.command({ originalEvent: event, item: item });
    }

    // prevent hash change
    if (item.items || (!item.url && !item.routerLink)) {
      setTimeout(() => {
        this.appMenu.layoutMenuScrollerViewChild.moveBar();
      }, 450);
      event.preventDefault();
    }

    // hide menu
    if (!item.items) {
      if (this.app.isMobile()) {
        this.app.sidebarActive = false;
        this.app.mobileMenuActive = false;
      }

      if (this.app.isHorizontal()) {
        this.app.resetMenu = true;
      } else {
        this.app.resetMenu = false;
      }

      this.app.menuHoverActive = !this.app.menuHoverActive;
    }
  }

  onMouseEnter(index: number) {
    if (this.root && this.app.menuHoverActive && this.app.isHorizontal()
      && !this.app.isMobile() && !this.app.isTablet()) {
      this.activeIndex = index;
    }
  }

  isActive(index: number): boolean {
    return this.activeIndex === index || this.item.expanded;
  }

  @Input() get reset(): boolean {
    return this._reset;
  }

  set reset(val: boolean) {
    this._reset = val;

    if (this._reset && (this.app.isHorizontal() || this.app.isOverlay())) {
      this.activeIndex = null;
    }
  }
}
