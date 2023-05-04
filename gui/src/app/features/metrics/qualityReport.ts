import { testResults } from "./testResults";

export interface qualityReport {
    testResults: testResults[];
    serialNumber: string;
}