import { useAuthenticationContext } from "../../../authentication/authenticationContext";
import { upload_controller_endpoints } from "../../apiEndpointsUrlsList";
import { fetchWithRequestId } from "../../fetchWithRequestId";

export const useUploadImage = () => {
    const { authentication } = useAuthenticationContext();

    const uploadImage = async (imageFile: File): Promise<string> => {
        const endpoint = upload_controller_endpoints.upload_image;
        const url = endpoint.url.toString(); // Convert URL object to string
        const formData = new FormData();
        formData.append("file", imageFile);

        const requestOptions = {
            method: endpoint.method,
            headers: {
                Authorization: `Bearer ${authentication.token}`,
            },
            body: formData,
        };

        const response = await fetchWithRequestId(url, requestOptions);

        if (!response.ok) {
            const errorText = await response.text();
            console.error(`Upload failed with status ${response.status}:`, errorText);
            throw new Error(`Upload failed (${response.status}): ${errorText || response.statusText}`);
        }

        const responseJson = await response.json();

        if (!responseJson.ok) {
            throw new Error(responseJson.message || "Failed to upload image.");
        }

        return responseJson.cdnUrl;
    };

    return { uploadImage };
};
