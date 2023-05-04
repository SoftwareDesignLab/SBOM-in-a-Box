import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SharedModule } from './shared/shared.module';
import { IconComponent } from './shared/components/icon/icon.component';
import { ButtonComponent } from './shared/components/button/button.component';
import { AccordionComponent } from './shared/components/accordion/accordion.component';
import { DropdownComponent } from './shared/components/dropdown/dropdown.component';
import { SidePanelComponent } from './shared/components/side-panel/side-panel.component';
import { UploadComponent } from './features/upload/upload.component';
import { HttpClientModule } from '@angular/common/http';
import { ComparisonComponent } from './features/comparison/comparison/comparison.component';
import {
  ComparisonPageComponent,
  ComparisonDialogComponent,
} from './features/comparison/comparison-page/comparison-page.component';
import { DialogComponent } from '@components/dialog/dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MetricsSidePanelComponent } from './features/metrics/metrics-side-panel/metrics-side-panel.component';
import { MetricsBodyComponent } from './features/metrics/metrics-body/metrics-body.component';
import { MetricsMainComponent } from './features/metrics/metrics-main/metrics-main.component';
import { MatStepperModule } from '@angular/material/stepper';


@NgModule({
  declarations: [
    AppComponent,
    IconComponent,
    ButtonComponent,
    ComparisonPageComponent,
    AccordionComponent,
    IconComponent,
    DropdownComponent,
    SidePanelComponent,
    UploadComponent,
    ComparisonComponent,
    DialogComponent,
    ComparisonDialogComponent,
    MetricsSidePanelComponent,
    MetricsBodyComponent,
    MetricsMainComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatExpansionModule,
    MatMenuModule,
    SharedModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatExpansionModule,
    MatStepperModule,
    HttpClientModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatStepperModule
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
