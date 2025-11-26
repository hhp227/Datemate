//
//  ProfileSetupView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import SwiftUI
import PhotosUI

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
                    .frame(maxWidth: .infinity)
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
        GeometryReader { proxy in
            let size = proxy.size
            
            ZStack(alignment: .topTrailing) {
                Image(uiImage: UIImage(contentsOfFile: url.path) ?? UIImage())
                    .resizable()
                    .scaledToFill()
                    .frame(width: size.width, height: size.height)
                    .clipped()
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                    )
                Button(action: onRemove) {
                    Image(systemName: "xmark")
                        .font(.system(size: 12))
                        .foregroundColor(.white)
                        .padding(4)
                        .background(Color.black.opacity(0.6))
                        .clipShape(Circle())
                }
                .offset(x: 4, y: -4)
            }
        }
        .aspectRatio(1, contentMode: .fit)
    }
}

struct PrimaryProfileImage: View {
    let url: URL
    
    let onRemove: () -> Void

    var body: some View {
        GeometryReader { proxy in
            ZStack(alignment: .topTrailing) {
                Image(uiImage: UIImage(contentsOfFile: url.path) ?? UIImage())
                    .resizable()
                    .scaledToFill()
                    .frame(width: proxy.size.width, height: 250) // 화면 너비로 고정
                    .clipped() // 영역 밖 이미지 제거
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(Color.blue, lineWidth: 2)
                    )
                Button(action: onRemove) {
                    Image(systemName: "xmark")
                        .font(.system(size: 14))
                        .foregroundColor(.white)
                        .padding(6)
                        .background(Color.black.opacity(0.6))
                        .clipShape(Circle())
                }
                .offset(x: 8, y: -8)
            }
        }
        .frame(height: 250) // 고정
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
