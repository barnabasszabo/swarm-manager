import { Component, OnInit } from '@angular/core';
import { SwarmService } from '../swarm.service';
import { Pool } from '../model/Pool';
import { MenuItem } from 'primeng/primeng';
import * as _ from 'lodash';

@Component({
  selector: 'app-top-menu',
  templateUrl: './top-menu.component.html',
  styleUrls: ['./top-menu.component.scss']
})
export class TopMenuComponent implements OnInit {

  items: MenuItem[];

  private poolInterval;
  private poolCache: Pool[];

  constructor(private swarmservice: SwarmService) { }

  ngOnInit() {
    this.poolInterval = setInterval(() => {
      this.refresh();
    }, 30000);
    this.refresh();
  }

  refresh() {
    console.log('Refreshing pool list in top menu');

    this.swarmservice.getPools().subscribe(data => {
      if (!_.isEqual(this.poolCache, data)) {
        console.log('Pool changed from the last check');

        this.poolCache = data;

        this.items = [
          { label: 'All', icon: 'subject', routerLink: ['/pool/all'] }
        ];
        this.poolCache.forEach(pool => {
          this.items.push({ label: pool.displayName, icon: 'subject', routerLink: ['pool/' + pool.id] });
        });

      }
    });
  }

}
