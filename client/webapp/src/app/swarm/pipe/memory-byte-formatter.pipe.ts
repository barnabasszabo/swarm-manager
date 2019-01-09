import { Pipe, PipeTransform } from '@angular/core';
import { SwarmTask } from '../model/SwarmTask';

@Pipe({
  name: 'memoryByteFormatter'
})
export class MemoryByteFormatterPipe implements PipeTransform {

  transform(task: SwarmTask, type: string): number {
    try {
      if ( type === 'limit' ) {
        return parseFloat((task.task.Spec.Resources.Limits.MemoryBytes / 1073741824).toFixed(2));
      } else {
        return parseFloat((task.task.Spec.Resources.Reservations.MemoryBytes / 1073741824).toFixed(2));
      }
    } catch (e) {
      return 0;
    }
  }

}
