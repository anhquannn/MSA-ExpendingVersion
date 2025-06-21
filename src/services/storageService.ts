import { User } from "../models/user.model";
import { createClient } from '@supabase/supabase-js';
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

LocalStorageManager.init();
const supabaseUrl = process.env.REACT_APP_SUPABASE_URL;
const supabaseKey = process.env.REACT_APP_SUPABASE_ANON_KEY;

// Kiểm tra xem các biến môi trường đã được cung cấp chưa
if (!supabaseUrl || !supabaseKey) {
    throw new Error("Supabase URL and Key are required. Please check your .env file.");
}
const supabase = createClient(supabaseUrl, supabaseKey);
export const storageService = {

    uploadImages: async (files: File[], bucketName: string): Promise<string[]> => {
        if (!files || files.length === 0) {
            return [];
        }

        console.log(`Bắt đầu upload ${files.length} file(s) lên bucket: ${bucketName}...`);

        // Wang Code Web: Sử dụng Promise.all để upload các file một cách đồng thời, tăng hiệu suất.
        const uploadPromises = files.map(file => {
            // Tạo một tên file duy nhất để tránh bị ghi đè
            // Ví dụ: ten-file-goc-1678886400000.png
            const fileExt = file.name.split('.').pop();
            const fileName = `${Math.random()}.${fileExt}`;
            const filePath = `${fileName}`;

            console.log(`Đang upload file: ${filePath}`);
            return supabase.storage
                .from(bucketName)
                .upload(filePath, file);
        });

        // Chờ tất cả các quá trình upload hoàn tất
        const results = await Promise.all(uploadPromises);

        // Xử lý kết quả và lấy URL công khai
        const urls = results.map(res => {
            if (res.error) {
                console.error('Lỗi khi upload file:', res.error.message);
                throw res.error; // Ném lỗi ra ngoài để React Query có thể bắt
            }

            // Lấy URL công khai của file vừa upload
            const { data } = supabase.storage
                .from(bucketName)
                .getPublicUrl(res.data.path);

            console.log(`Upload thành công, URL: ${data.publicUrl}`);
            return data.publicUrl;
        });

        return urls;
    },
};

export async function uploadMultipleImages(files: File[], folderName: string): Promise<string[]> {
  const uploadPromises = files.map(file => {
    return new Promise<string>(async (resolve, reject) => {
      try {
        const fileExt = file.name.split('.').pop();
        const fileName = `${crypto.randomUUID()}.${fileExt}`;
        const filePath = `${folderName}/${fileName}`;
        const { data: uploadData, error: uploadError } = await supabase.storage
          .from('msa') 
          .upload(filePath, file, {
            cacheControl: '2592000000', 
            upsert: false 
          });

        if (uploadError) {
          throw new Error(`Lỗi khi upload file ${file.name}: ${uploadError.message}`);
        }
        const confirmedPath = uploadData.path; 
        
        const { data: urlData } = supabase.storage
          .from('msa')
          .getPublicUrl(confirmedPath); 
        
        if (!urlData.publicUrl) {
            throw new Error(`Không thể lấy public URL cho file: ${confirmedPath}`);
        }

        resolve(urlData.publicUrl);

      } catch (error) {
        reject(error);
      }
    });
  });
  try {
    const publicUrls = await Promise.all(uploadPromises);
    return publicUrls;
  } catch (error) {
    console.error("Một hoặc nhiều file đã upload thất bại:", error);
    throw error;
  }
}
export { LocalStorageManager };
export type { User, Branch, AuthTokens };

function uuidv4() {
    throw new Error("Function not implemented.");
}
