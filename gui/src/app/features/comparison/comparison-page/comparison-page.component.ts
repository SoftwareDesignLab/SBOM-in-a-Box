/** @Author Tina DiLorenzo, Justin Jantzi */

import { Component, EventEmitter, Inject, Input, Output } from "@angular/core";
import { Comparison } from "../comparison";
import { SBOM } from "@models/sbom";

import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { DataHandlerService } from "@services/data-handler.service";

@Component({
  selector: "app-comparison-page",
  templateUrl: "./comparison-page.component.html",
  styleUrls: ["./comparison-page.component.css"],
})
export class ComparisonPageComponent {
  collapsed: boolean = false;

  sboms: string[] = ["a", "b"];
  targetSbom!: string;
  compareTo!: string;

  constructor(
    public dialog: MatDialog,
    private dataHandler: DataHandlerService
  ) {}

  /** @TODO create an api call where you would send the target sbom and compare */
  // it against all sboms rather than doing singular api calls for each one  */
  selectTargetSbom($event: any) {
    this.targetSbom = $event;
  }

  /** @TODO replace with inserting the associated diff report */
  selectComparison(value: string) {
    this.compareTo = value;
  }

  // Display diff report
  compare() {
    this.dataHandler.Compare(this.targetSbom, [this.compareTo]);
  }

  openDialog(sbom: SBOM): void {
    const dialogRef = this.dialog.open(ComparisonDialogComponent, {
      data: sbom,
    });
  }

  GetValidSBOMs() {
    return this.dataHandler.GetValidSBOMs();
  }

  getSBOMAlias(path: string) {
    return this.dataHandler.getSBOMAlias(path);
  }

  GetComparison() {
    return this.dataHandler.comparison;
  }
}

@Component({
  selector: "app-comparison-dialog",
  template: `<app-dialog
    icon="delete"
    (clicked)="this.dialogRef.close(true)"
    buttonText="delete"
  >
    <span title>SBOM details</span>
    <div body>
      Identifier: {{ data.name }} Timestamp: {{ data.timestamp }} Publisher:
      {{ data.publisher }}
      <app-button (click)="this.dialogRef.close(false)">Close</app-button>
    </div>
  </app-dialog>`,
  styles: [],
})
export class ComparisonDialogComponent {
  @Output() removeSbom: EventEmitter<SBOM> = new EventEmitter<SBOM>();
  constructor(
    public dialogRef: MatDialogRef<ComparisonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SBOM
  ) {}
}
