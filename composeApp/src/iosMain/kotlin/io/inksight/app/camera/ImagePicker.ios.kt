package io.inksight.app.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

@Composable
actual fun rememberGalleryPickerLauncher(
    onResult: (ImagePickResult?) -> Unit,
): () -> Unit {
    val callback = remember { onResult }
    return remember {
        { launchGalleryPicker(callback) }
    }
}

@Composable
actual fun rememberCameraCaptureLauncher(
    onResult: (ImagePickResult?) -> Unit,
): () -> Unit {
    val callback = remember { onResult }
    return remember {
        { launchCameraPicker(callback) }
    }
}

private fun launchGalleryPicker(onResult: (ImagePickResult?) -> Unit) {
    val config = PHPickerConfiguration().apply {
        filter = PHPickerFilter.imagesFilter
        selectionLimit = 1
    }
    val picker = PHPickerViewController(configuration = config)
    val delegate = GalleryPickerDelegate(onResult)
    picker.setDelegate(delegate)
    getRootViewController()?.presentViewController(picker, animated = true, completion = null)
}

private fun launchCameraPicker(onResult: (ImagePickResult?) -> Unit) {
    if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
        onResult(null)
        return
    }
    val picker = UIImagePickerController().apply {
        sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
    }
    val delegate = CameraPickerDelegate(onResult)
    picker.setDelegate(delegate)
    getRootViewController()?.presentViewController(picker, animated = true, completion = null)
}

private class GalleryPickerDelegate(
    private val onResult: (ImagePickResult?) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, null)
        val result = didFinishPicking.firstOrNull() as? PHPickerResult
        if (result == null) {
            onResult(null)
            return
        }
        result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
            if (data != null) {
                val image = UIImage(data = data)
                val jpegData = UIImageJPEGRepresentation(image, 0.95)
                if (jpegData != null) {
                    onResult(ImagePickResult(imageBytes = jpegData.toByteArray(), mimeType = "image/jpeg"))
                } else {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }
    }
}

private class CameraPickerDelegate(
    private val onResult: (ImagePickResult?) -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        picker.dismissViewControllerAnimated(true, null)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        if (image != null) {
            val jpegData = UIImageJPEGRepresentation(image, 0.95)
            if (jpegData != null) {
                onResult(ImagePickResult(imageBytes = jpegData.toByteArray(), mimeType = "image/jpeg"))
            } else {
                onResult(null)
            }
        } else {
            onResult(null)
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
        onResult(null)
    }
}

private fun getRootViewController() =
    UIApplication.sharedApplication.keyWindow?.rootViewController

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    val bytes = ByteArray(size)
    if (size > 0) {
        bytes.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return bytes
}
