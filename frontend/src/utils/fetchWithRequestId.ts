import { v4 as uuidv4 } from 'uuid';

/**
 * 增强的 fetch 函数，自动为每个请求添加唯一的 X-Request-ID 头
 */
export async function fetchWithRequestId(
    input: RequestInfo | URL,
    init?: RequestInit
): Promise<Response> {
    const requestId = uuidv4();

    // 合并 headers，添加 X-Request-ID
    const headers = new Headers(init?.headers);
    headers.set('X-Request-ID', requestId);

    // 记录请求日志（可选，用于调试）
    let url: string;
    if (typeof input === 'string') {
        url = input;
    } else if (input instanceof URL) {
        url = input.toString();
    } else {
        url = input.url;
    }
    console.log(`[Request ${requestId}] ${init?.method || 'GET'} ${url}`);

    try {
        const response = await fetch(input, {
            ...init,
            headers,
        });

        // 记录响应日志（可选，用于调试）
        console.log(`[Response ${requestId}] ${response.status} ${response.statusText}`);

        return response;
    } catch (error) {
        // 记录错误日志
        console.error(`[Error ${requestId}]`, error);
        throw error;
    }
}

/**
 * 从现有的 RequestInit 中提取或生成 requestId
 */
export function getOrCreateRequestId(init?: RequestInit): string {
    const headers = new Headers(init?.headers);
    const existingId = headers.get('X-Request-ID');
    return existingId || uuidv4();
}
