import {DictionaryEntry} from 'micro_service_modules/api-kaccess';

export interface TpbcDataInterface {
    title?: string;
    type?: BarChartType;
    legendXAxis?: string;
    legendYAxis?: string;
    series?: Serie[];
    xLabels?: string[];
    barModels?: BarModel[];
    groupedModels?: GroupedBarModel[];
    lineAndPlot?: LineAndPlot[];
    colors?: string[];
}

export interface BarModel {
    coordonateX?: string;
    coordonateY?: number[];
}

export interface GroupedBarModel {
    coordonateX: string;
    [key: string]: any;
}


export interface LineAndPlot {
    color?: string;
    values?: LineAndPlotValue[];
}

export interface LineAndPlotValue {
    coordonateX?: string;
    coordonateY?: number;
    color?: string;
}


export interface Serie {
    name?: string;
    color?: string;
}

export interface BarChartData {
    legend?: DictionaryEntry[];
    type?: BarChartType;
    legendYAxis?: DictionaryEntry[];
    legendXAxis?: DictionaryEntry[];
    series?: BarChartSeries[];
    values?: BarChartValues[];
}

export interface BarChartSeries {
    legend?: DictionaryEntry[];
}

export interface BarChartValues {
    colour?: string;
    borderColour?: string;
    legend?: DictionaryEntry[];
    values?: number[];
}

export type BarChartType = 'POINT' | 'LINE' | 'BAR';

export const BarChartType = {
    Point: 'POINT' as BarChartType,
    Line: 'LINE' as BarChartType,
    Bar: 'BAR' as BarChartType
};

export const DEFAULT_HEIGHT = 1000;
