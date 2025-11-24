//
//  ProfileSetupView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import SwiftUI
import PhotosUI

struct ProfileSetupView: View {
    @StateObject var viewModel: ProfileSetupViewModel = DependencyContainer.instance.provideProfileSetupViewModel()
    
    @FocusState private var focusedField: Field?
    
    let onSetupComplete: () -> Void
    
    @State private var showingImagePicker = false
    
    @State private var selectedPhotos: [PhotosPickerItem] = []
    
    private let maxImages = 7
    
    var body: some View {
        ScrollView {
            VStack(alignment: .center, spacing: 16) {
                Text("Setup Your Profile")
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(.blue)
                    .padding(.bottom, 40)
                Text("성별을 선택해 주세요")
                    .frame(maxWidth: .infinity, alignment: .leading)
                GenderSelector(
                    selectedGender: viewModel.uiState.selectedGender,
                    onGenderSelected: viewModel.onGenderSelected
                )
                VStack(alignment: .leading, spacing: 8) {
                    TextField("Nickname", text: $viewModel.uiState.nickname)
                        .textFieldStyle(.roundedBorder)
                        .focused($focusedField, equals: .nickname)
                        .onChange(of: viewModel.uiState.nickname) { newValue in
                            viewModel.onNicknameChange(newValue)
                        }
                        if let error = viewModel.uiState.nicknameError {
                            Text(error).foregroundColor(.red).font(.caption)
                        }
                }
                Text("Profile Photos (\(viewModel.uiState.selectedImageUrls.count) / Max \(maxImages))")
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity, alignment: .leading)
                if viewModel.uiState.selectedImageUrls.isEmpty {
                    PrimaryImageAddButton {
                        showingImagePicker = true
                    }
                } else {
                    PrimaryProfileImage(
                        url: viewModel.uiState.selectedImageUrls.first!,
                        onRemove: { viewModel.removeImage(at: 0) }
                    )
                    LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: 3), spacing: 8) {
                        if viewModel.uiState.selectedImageUrls.count < maxImages {
                            SmallImageAddButton {
                                showingImagePicker = true
                            }
                        }
                        ForEach(Array(viewModel.uiState.selectedImageUrls.dropFirst().enumerated()), id: \.offset) { index, url in
                            SelectedProfileImage(
                                url: url,
                                onRemove: { viewModel.removeImage(at: index + 1) }
                            )
                        }
                    }
                    .padding(.vertical, 8)
                }
                Button(action: viewModel.completeProfileSetup) {
                    if viewModel.uiState.isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle())
                            .frame(maxWidth: .infinity, minHeight: 52)
                    } else {
                        Text("완료")
                            .frame(maxWidth: .infinity, minHeight: 52)
                            .foregroundColor(.white)
                            .background(viewModel.uiState.isSubmitEnabled ? Color.blue : Color.gray)
                            .cornerRadius(16)
                    }
                }
                .disabled(!viewModel.uiState.isSubmitEnabled || viewModel.uiState.isLoading)
            }
            .padding(24)
        }
        .photosPicker(isPresented: $showingImagePicker, selection: $selectedPhotos, matching: .images, photoLibrary: .shared())
        .onChange(of: selectedPhotos) { newItems in
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
        .onChange(of: viewModel.uiState.isSetupComplete) { complete in
            if complete {
                onSetupComplete()
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
    
    enum Field { case nickname }
}

struct PrimaryImageAddButton: View {
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            ZStack {
                RoundedRectangle(cornerRadius: 16)
                    .strokeBorder(Color.blue, lineWidth: 2)
                    .frame(height: 250)
                VStack {
                    Image(systemName: "plus")
                        .resizable()
                        .frame(width: 48, height: 48)
                        .foregroundColor(.blue)
                    Text("대표 사진 추가").foregroundColor(.blue)
                }
            }
        }
    }
}

struct SmallImageAddButton: View {
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .strokeBorder(Color.blue.opacity(0.6), lineWidth: 1)
                    .aspectRatio(1, contentMode: .fit)
                Image(systemName: "plus")
                    .foregroundColor(.blue.opacity(0.8))
                    .frame(width: 24, height: 24)
            }
        }
    }
}

struct SelectedProfileImage: View {
    let url: URL
    
    let onRemove: () -> Void

    var body: some View {
        ZStack(alignment: .topTrailing) {
            AsyncImage(url: url) { image in
                image.resizable()
                     .scaledToFill()
            } placeholder: {
                Color.gray.opacity(0.3)
            }
            .frame(maxWidth: .infinity)
            .aspectRatio(1, contentMode: .fit)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.gray.opacity(0.5), lineWidth: 1))
            Button(action: onRemove) {
                Image(systemName: "xmark")
                    .font(.caption)
                    .foregroundColor(.white)
                    .padding(6)
                    .background(Circle().fill(Color.black.opacity(0.6)))
            }
            .offset(x: 4, y: -4)
        }
    }
}

struct PrimaryProfileImage: View {
    let url: URL
    
    let onRemove: () -> Void

    var body: some View {
        ZStack(alignment: .topTrailing) {
            AsyncImage(url: url) { image in
                image.resizable()
                     .scaledToFill()
            } placeholder: {
                Color.gray.opacity(0.3)
            }
            .frame(height: 250)
            .clipShape(RoundedRectangle(cornerRadius: 16))
            .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.blue, lineWidth: 2))
            Button(action: onRemove) {
                Image(systemName: "xmark")
                    .foregroundColor(.white)
                    .padding(8)
                    .background(Circle().fill(Color.black.opacity(0.6)))
                    .overlay(Circle().stroke(Color.white, lineWidth: 2))
            }
            .offset(x: 8, y: -8)
        }
    }
}

struct GenderSelector: View {
    let selectedGender: Gender?
    
    let onGenderSelected: (Gender) -> Void

    var body: some View {
        HStack(spacing: 16) {
            GenderChip(gender: .male, label: "남성", isSelected: selectedGender == .male, onClick: { onGenderSelected(.male) })
            GenderChip(gender: .female, label: "여성", isSelected: selectedGender == .female, onClick: { onGenderSelected(.female) })
        }
    }
}

struct GenderChip: View {
    let gender: Gender
    
    let label: String
    
    let isSelected: Bool
    
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            Text(label)
                .foregroundColor(isSelected ? .white : .gray)
                .frame(maxWidth: .infinity, minHeight: 48)
                .background(isSelected ? Color.blue : Color.gray.opacity(0.5))
                .cornerRadius(8)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(isSelected ? Color.clear : Color.gray.opacity(0.5), lineWidth: 1)
                )
        }
    }
}

struct ProfileSetupView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileSetupView(onSetupComplete: {})
    }
}
