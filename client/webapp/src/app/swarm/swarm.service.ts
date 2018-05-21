import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pool } from './model/Pool';
import { Ecosystem } from './model/Ecosystem';
import { PoolResource } from './model/PoolResource';
import { LinkGroup } from './model/LinkGroup';

@Injectable({
  providedIn: 'root'
})
export class SwarmService {

  constructor(private http: HttpClient) { }

  getPools(): Observable<Pool[]> {
    return this.http.get<Pool[]>('/api/pool');
  }

  getPool(id: string): Observable<Pool> {
    return this.http.get<Pool>('/api/pool/' + id);
  }

  getPoolResource(poolId: string): Observable<PoolResource> {
    return this.http.get<PoolResource>('/api/pool/' + poolId + '/resource');
  }

  getPoolLink(poolId: string): Observable<LinkGroup[]> {
    return this.http.get<LinkGroup[]>('/api/pool/' + poolId + '/link');
  }

  setPoolLink(poolId: string, links: LinkGroup[]): Observable<LinkGroup[]> {
    return this.http.post<LinkGroup[]>('/api/pool/' + poolId + '/link', links);
  }

  getEcosystems(poolId: string): Observable<Ecosystem[]> {
    return this.http.get<Ecosystem[]>('/api/pool/' + poolId + '/ecosystem');
  }

}
