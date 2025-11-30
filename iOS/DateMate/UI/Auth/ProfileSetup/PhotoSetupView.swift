//
//  PhotoSetupView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/26.
//

import SwiftUI
import _PhotosUI_SwiftUI

struct PhotoSetupView: View {
    @ObservedObject var viewModel: ProfileSetupViewModel
    
    @State private var selectedItems: [PhotosPickerItem] = []
    
    @State private var showingImagePicker = false
        
    private let maxImages = 7
    
    var onNext: () -> Void
    
    var body: some View {
        VStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("나를 잘 나타내는 사진을 올려주세요.")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                        .padding(.top, 16)
                    Text("Profile Photos (\(viewModel.uiState.selectedImageUrls.count) / Max \(maxImages))")
                        .font(.subheadline)
                        .bold()
                    if viewModel.uiState.selectedImageUrls.isEmpty {
                        PrimaryImageAddButton {
                            showingImagePicker = true
                        }
                    } else {
                        PrimaryProfileImage(
                            url: viewModel.uiState.selectedImageUrls.first!,
                            onRemove: { viewModel.removeImage(viewModel.uiState.selectedImageUrls.first!) }
                        )
                        LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: 3), spacing: 8) {
                            ForEach(Array(viewModel.uiState.selectedImageUrls.dropFirst()), id: \.self) { url in
                                SelectedProfileImage(
                                    url: url,
                                    onRemove: { viewModel.removeImage(url) }
                                )
                                .aspectRatio(1, contentMode: .fit)
                            }
                            if viewModel.uiState.selectedImageUrls.count < maxImages {
                                SmallImageAddButton {
                                    showingImagePicker = true
                                }
                                .aspectRatio(1, contentMode: .fit)
                            }
                        }
                        .padding(.vertical, 8)
                    }
                }
                .padding(.horizontal, 24)
            }
            Button(action: onNext) {
                Text("다음")
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity, minHeight: 52)
                    .background(viewModel.uiState.selectedImageUrls.isEmpty ? Color.gray : Color.blue)
                    .cornerRadius(16)
            }
            .disabled(viewModel.uiState.selectedImageUrls.isEmpty)
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .navigationTitle("프로필 사진 설정 (2/3)")
        .navigationBarTitleDisplayMode(.inline)
        .photosPicker(isPresented: $showingImagePicker, selection: $selectedItems, matching: .images, photoLibrary: .shared())
        .onChange(of: selectedItems) { newItems in
            Task {
                var urls: [URL] = []
                
                for item in newItems {
                    if let data = try? await item.loadTransferable(type: Data.self),
                       let tempURL = saveTempFile(data: data) {
                        urls.append(tempURL)
                    }
                }
                viewModel.onImagesSelected(urls)
            }
        }
    }

    func saveTempFile(data: Data) -> URL? {
        let tempDir = FileManager.default.temporaryDirectory
        let fileURL = tempDir.appendingPathComponent(UUID().uuidString + ".jpg")
        
        do {
            try data.write(to: fileURL)
            return fileURL
        } catch {
            print("Failed to save temp file: \(error)")
            return nil
        }
    }
}

struct PhotoSetupView_Previews: PreviewProvider {
    static var previews: some View {
        PhotoSetupView(viewModel: DependencyContainer.instance.provideProfileSetupViewModel(), onNext: {})
    }
}
