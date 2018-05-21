import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SwarmService } from '../../swarm.service';
import { Pool } from '../../model/Pool';
import { Ecosystem } from '../../model/Ecosystem';

import * as _ from 'lodash';
import { LinkGroup } from '../../model/LinkGroup';

@Component({
  selector: 'app-pool-ecosystem-page',
  templateUrl: './pool-ecosystem-page.component.html',
  styleUrls: ['./pool-ecosystem-page.component.scss']
})
export class PoolEcosystemPageComponent {

  pool: Pool;
  poolLinkGroup: LinkGroup[];
  poolLinkGroupJSON = '';
  poolLinkGroupDisplay = false;
  ecosystems: Ecosystem[];
  host = window.location.hostname;

  poolOverviewCpuChart = {};
  poolOverviewMemoryChart = {};
  chartOptions = {
    legend: { display: false },
    scales: {
      yAxes: [{
        ticks: {
          beginAtZero: true
        }
      }]
    }
  };

  private ecoRefreshInterval;
  private linkRefreshInterval;

  constructor(private route: ActivatedRoute, private swarmService: SwarmService) {
    route.params.subscribe(val => {
      if (val.id !== 'all') {
        this.swarmService.getPool(val.id).subscribe(poolData => {
          this.pool = poolData;

          clearInterval(this.linkRefreshInterval);
          this.linkRefreshInterval = setInterval(() => {
            this.refreshPoolLink();
          }, 5000);
          this.refreshPoolLink();

          clearInterval(this.ecoRefreshInterval);
          this.ecoRefreshInterval = setInterval(() => {
            this.refreshEcosystem();
          }, 5000);
          this.refreshEcosystem();
        });
      }
    });
  }

  refreshEcosystem(): void {
    console.log('Collecting ecosystems for ' + this.pool.displayName);

    this.swarmService.getEcosystems(this.pool.id).subscribe(newEcosystems => {
      newEcosystems = this.cleanupEcosystems(newEcosystems);
      if (!_.isEqual(this.ecosystems, newEcosystems)) {
        this.ecosystems = newEcosystems;
        this.refreshChart();
        console.log('New ecosystems are ', this.ecosystems);
      }
    });
  }

  refreshPoolLink(): void {
    this.swarmService.getPoolLink(this.pool.id).subscribe(data => {
      if (!_.isEqual(data, this.poolLinkGroup)) {
        this.poolLinkGroup = data;
      }
    });
  }

  cleanupEcosystems(ecosystems: Ecosystem[]): Ecosystem[] {
    const failedStates = ['SHUTDOWN', 'FAILED', 'REJECTED'];
    ecosystems.forEach(eco => {
      for (let i = eco.tasks.length - 1; i >= 0; i--) {
        if (failedStates.indexOf(eco.tasks[i].task.Status.State.toUpperCase()) > -1) {
          eco.tasks.splice(i, 1);
        }
      }
    });
    return ecosystems;
  }

  refreshChart(): void {
    const goodColor = '#9CCC65';
    const badColor = '#e91e63';
    this.swarmService.getPoolResource(this.pool.id).subscribe(data => {
      console.log('Pool resources are ', data);

      this.poolOverviewCpuChart = {
        labels: ['CPU core'],
        datasets: [
          {
            label: 'Declared CPU cores',
            backgroundColor: data.used.reservedCpuInCore < data.dedicated.reservedCpuInCore ? goodColor : badColor,
            borderColor: data.used.reservedCpuInCore < data.dedicated.reservedCpuInCore ? goodColor : badColor,
            data: [data.used.reservedCpuInCore]
          },
          {
            label: 'Total CPU core for pool',
            backgroundColor: '#42A5F5',
            borderColor: '#1E88E5',
            data: [data.dedicated.reservedCpuInCore]
          }
        ]
      };
      this.poolOverviewMemoryChart = {
        labels: ['Memory (GB)'],
        datasets: [
          {
            label: 'Declared Memory (GB)',
            backgroundColor: data.used.reservedMemoryInGB < data.dedicated.reservedMemoryInGB ? goodColor : badColor,
            borderColor: data.used.reservedMemoryInGB < data.dedicated.reservedMemoryInGB ? goodColor : badColor,
            data: [data.used.reservedMemoryInGB]
          },
          {
            label: 'Total Memory (GB) for pool',
            backgroundColor: '#42A5F5',
            borderColor: '#1E88E5',
            data: [data.dedicated.reservedMemoryInGB]
          }
        ]
      };
    });
  }

  editLinks(): void {
    this.poolLinkGroupDisplay = true;
    this.poolLinkGroupJSON = JSON.stringify(this.poolLinkGroup, null, 4);
  }

  isLinksInvalid(): boolean {
    try {
      JSON.parse(this.poolLinkGroupJSON);
      return false;
    } catch (e) {
      return true;
    }
  }
  saveNewLinks(): void {
    this.poolLinkGroupDisplay = false;
    try {
      const newLinks = JSON.parse(this.poolLinkGroupJSON);
      this.swarmService.setPoolLink(this.pool.id, newLinks).subscribe(data => {
        this.refreshPoolLink();
      });
    } catch (e) { }
  }

}


