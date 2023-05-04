import { Injectable } from '@angular/core';
import { ClientService } from './client.service';
import { HttpParams } from '@angular/common/http';
import { IpcRenderer } from 'electron';
import { Comparison } from '@features/comparison/comparison';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataHandlerService {

  private ipc!: IpcRenderer;
  public filePaths: string[] = [];

  public metrics: { [id: string]: Object | null } = {};
  public comparison!: Comparison;

  public loading = new BehaviorSubject<boolean>(false);

  public selectedQualityReport!: string;

  constructor(private client: ClientService) {
    if (window.require) {
      try {
        this.ipc = window.require('electron').ipcRenderer;
      } catch (e) {
        console.log(e);
      }
    } else {
      console.warn('App not running inside Electron!');
    }
  }

  AddFiles(paths: string[]) {
    this.loading.next(true);
    this.filePaths.push(...paths);

    paths.forEach((path) => {
      this.RunMetricsOnFile(path);
    })
  }

  RunAllMetrics() {
    this.filePaths.forEach((file) => {
      this.RunMetricsOnFile(file);
    })
  }

  RunMetricsOnFile(path: string) {
    this.ipc.invoke('getFileData', path).then((data: any) => {
      this.client.post("qa", new HttpParams().set("contents",data).set("fileName", path)).subscribe((result) => {
        this.metrics[path] = result;
        this.loading.next(false);
      },
      (error) => {
        this.metrics[path] = null;
        this.loading.next(false);
      })
    });
  }

  GetValidSBOMs() {
    return Object.keys(this.metrics).filter((x) => this.metrics[x] !== null);
  }

  getSBOMAlias(path: string) {
    const index = this.filePaths.indexOf(path);
    return `SBOM ${index}`;
  }

  async Compare(main: string, others: string[]): Promise<any> {
    let toSend: { [path: string]: any } = {};
    let total = others.length + 1;
    let i = 0;

    let paths = [main, ...others];

    paths.forEach((path) => {
      this.ipc.invoke('getFileData', path).then((data: any) => {
        toSend[path] = data;
        i++;

        //last time running
        if(i == total) {
          console.log("last running");

          let fileData: string[] = [];
          let filePaths: string[] = [];

          //Ensure that the compare is first in list
          Object.keys(toSend).forEach((path) => {
            console.log("insert path: " + path);
            if(path === main) {
              fileData.unshift(toSend[path]);
              filePaths.unshift(path);
            } else {
              fileData.push(toSend[path]);
              filePaths.push(path);
            }
          })

          this.client.post("compare", new HttpParams().set('contents', JSON.stringify(fileData)).set('fileNames', JSON.stringify(filePaths))).subscribe((result: any) => {
            this.comparison = result;
          })
        }
      })
    })
  }

}
