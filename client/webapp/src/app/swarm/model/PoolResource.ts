import { Pool } from './Pool';
import { SwarmResource } from './SwarmResource';

export class PoolResource {
  pool: Pool;
  dedicated: SwarmResource;
  used: SwarmResource;
}
