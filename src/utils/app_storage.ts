import { User } from "../models/user.model";
interface Branch {
  branchId: number;
  name: string;
  address?: string;
  city?: string;
  district?: string;
  phone?: string;
  // Thêm các trường khác của branch nếu có
}
// Định nghĩa kiểu dữ liệu cho token
interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

// Định nghĩa một Model để lưu trữ tất cả trong Local Storage
interface AppStorageModel {
  user?: User;
  branch?: Branch;
  authTokens?: AuthTokens;
}


class LocalStorageManager {
  private static readonly STORAGE_KEY = 'app_data'; // Khóa chính để lưu trữ tất cả dữ liệu
  private static _data: AppStorageModel = {}; // Biến cache trong bộ nhớ

  // Khởi tạo: Tải dữ liệu từ Local Storage khi lớp được tạo lần đầu
  static init(): void {
    if (typeof localStorage === 'undefined') {
      console.warn("localStorage is not available. Running in a non-browser environment.");
      return;
    }
    const storedData = localStorage.getItem(LocalStorageManager.STORAGE_KEY);
    if (storedData) {
      try {
        LocalStorageManager._data = JSON.parse(storedData);
        console.log("Data loaded from localStorage:", LocalStorageManager._data);
      } catch (e) {
        console.error("Failed to parse data from localStorage:", e);
        LocalStorageManager._data = {}; // Xóa dữ liệu lỗi
        localStorage.removeItem(LocalStorageManager.STORAGE_KEY); // Xóa mục lỗi
      }
    } else {
      console.log("No data found in localStorage.");
    }
  }

  // Phương thức private để lưu dữ liệu vào Local Storage
  private static _saveData(): void {
    if (typeof localStorage === 'undefined') return;
    try {
      localStorage.setItem(LocalStorageManager.STORAGE_KEY, JSON.stringify(LocalStorageManager._data));
    } catch (e) {
      console.error("Failed to save data to localStorage:", e);
    }
  }

  // --- Hàm lưu dữ liệu ---

  static saveUser(user: User): void {
    LocalStorageManager._data.user = user;
    LocalStorageManager._saveData();
  }

  static saveBranch(branch: Branch): void {
    LocalStorageManager._data.branch = branch;
    LocalStorageManager._saveData();
  }

  static saveAuthTokens(tokens: AuthTokens): void {
    LocalStorageManager._data.authTokens = tokens;
    LocalStorageManager._saveData();
  }

  // --- Hàm lấy dữ liệu ---

  static getUser(): User | undefined {
    return LocalStorageManager._data.user;
  }

  static getBranch(): Branch | undefined {
    return LocalStorageManager._data.branch;
  }

  static getAuthTokens(): AuthTokens | undefined {
    return LocalStorageManager._data.authTokens;
  }

  static getAccessToken(): string | undefined {
    return LocalStorageManager._data.authTokens?.accessToken;
  }

  static getRefreshToken(): string | undefined {
    return LocalStorageManager._data.authTokens?.refreshToken;
  }

  // --- Hàm xóa dữ liệu ---

  static clearUser(): void {
    delete LocalStorageManager._data.user;
    LocalStorageManager._saveData();
  }

  static clearBranch(): void {
    delete LocalStorageManager._data.branch;
    LocalStorageManager._saveData();
  }

  static clearAuthTokens(): void {
    delete LocalStorageManager._data.authTokens;
    LocalStorageManager._saveData();
  }

  static clearAllData(): void {
    LocalStorageManager._data = {};
    LocalStorageManager._saveData();
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(LocalStorageManager.STORAGE_KEY); // Xóa hẳn khỏi localStorage
    }
  }
}

// Tự động khởi tạo khi file được import/script được chạy
// Điều này đảm bảo dữ liệu được tải ngay khi web mở
LocalStorageManager.init();

// Export LocalStorageManager để có thể sử dụng ở các file khác
export { LocalStorageManager };
export type { User, Branch, AuthTokens };