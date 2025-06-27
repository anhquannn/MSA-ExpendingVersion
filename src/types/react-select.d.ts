declare module 'react-select' {
  import { ComponentType } from 'react';
  export interface OptionBase {
    value: any;
    label: string;
  }
  export type MultiValue<T> = readonly T[];

  const Select: ComponentType<any>;
  export default Select;
}
