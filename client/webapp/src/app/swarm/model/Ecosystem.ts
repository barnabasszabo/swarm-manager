import { EcosystemStatus } from './EcosystemStatus';
import { SwarmResource } from './SwarmResource';
import { SwarmTask } from './SwarmTask';
import { Pool } from './Pool';

export class Ecosystem {

  name: string;

  isStack: boolean;

  status: EcosystemStatus;

  usedResource: SwarmResource;

  markedAsRemove: boolean;

  markedMessage?: string[];

  overloaded: boolean;

  tasks: SwarmTask[];

  review: any; // TODO model the github api!

  pools: Pool[];

  labels: any;

  portConfig: any[]; // TODO model the github api!

}
