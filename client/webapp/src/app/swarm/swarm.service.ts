import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pool } from './model/Pool';
import { Ecosystem } from './model/Ecosystem';
import { PoolResource } from './model/PoolResource';

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

  getEcosystems(poolId: string): Observable<Ecosystem[]> {
    return this.http.get<Ecosystem[]>('/api/pool/' + poolId + '/ecosystem');
  }

}
