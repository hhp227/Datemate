//
//  MyProfileView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/12/01.
//

import SwiftUI

struct MyProfileView: View {
    @StateObject var viewModel: MyProfileViewModel = DependencyContainer.instance.provideMyProfileViewModel()
    
    var body: some View {
        Group {
            if viewModel.uiState.isLoading {
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if let message = viewModel.uiState.message, !message.isEmpty {
                Text("Error fetching profile")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ProfileContent(
                    profile: viewModel.uiState.profile,
                    stats: viewModel.uiState.stats,
                    posts: viewModel.uiState.posts
                )
            }
        }
    }
}

struct ProfileContent: View {
    let profile: Profile?
    
    let stats: UserStats
    
    let posts: [Post]
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 20) {
                HStack(alignment: .center, spacing: 20) {
                    AsyncImage(url: URL(string: profile?.photos.first ?? "https://picsum.photos/200")) { img in
                        img.resizable()
                    } placeholder: {
                        Color.gray.opacity(0.3)
                    }
                    .frame(width: 100, height: 100)
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.gray.opacity(0.3), lineWidth: 1))
                    
                    HStack(spacing: 20) {
                        ProfileStatItem(label: "Post", count: "\(stats.postCount)")
                        ProfileStatItem(label: "Followers", count: stats.followers)
                        ProfileStatItem(label: "Following", count: stats.following)
                    }
                }
                .padding(.top, 20)
                VStack(alignment: .leading, spacing: 6) {
                    Text(profile?.name ?? "Unknown User")
                        .font(.title3)
                        .fontWeight(.bold)
                    Text(profile?.job ?? "No Job Title")
                        .font(.subheadline)
                    Text(profile?.bio ?? "No bio available.")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                        .lineLimit(2)
                    Text("More...")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                }
                HStack {
                    ProfileTabButton(text: "Post", isSelected: true)
                    ProfileTabButton(text: "Videos", isSelected: false)
                    ProfileTabButton(text: "Tag", isSelected: false)
                }
                .padding(.top, 10)
                ForEach(posts, id: \.id) { post in
                    PostCardItem(post: post)
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 20)
        }
    }
}

struct ProfileStatItem: View {
    let label: String
    
    let count: String
    
    var body: some View {
        VStack {
            Text(label)
                .font(.caption)
            Text(count)
                .font(.title3)
                .fontWeight(.bold)
        }
        .frame(maxWidth: .infinity)
    }
}

struct ProfileTabButton: View {
    let text: String
    
    let isSelected: Bool
    
    var body: some View {
        Text(text)
            .fontWeight(.semibold)
            .frame(width: 100, height: 40)
            .background(isSelected ? Color.blue : Color.clear)
            .foregroundColor(isSelected ? .white : .gray)
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color.gray.opacity(0.3), lineWidth: isSelected ? 0 : 1)
            )
    }
}

struct PostCardItem: View {
    let post: Post
    
    var body: some View {
        ZStack(alignment: .bottomLeading) {
            AsyncImage(url: URL(string: post.imageUrls.first ?? "")) { phase in
                if let img = phase.image {
                    img
                        .resizable()
                        .scaledToFill()          // 비율 유지하면서 카드에 맞춤
                        .frame(height: 200)      // 카드 높이 고정
                        .clipped()               // 넘치는 영역 잘라내기
                        .cornerRadius(16)
                } else if phase.error != nil {
                    Color.gray
                        .frame(height: 200)
                        .cornerRadius(16)
                } else {
                    ProgressView()
                        .frame(height: 200)
                        .frame(maxWidth: .infinity)
                }
            }
            LinearGradient(
                colors: [.clear, .black.opacity(0.7)],
                startPoint: .top,
                endPoint: .bottom
            )
            .cornerRadius(16)
            .frame(height: 200)
            VStack(alignment: .leading, spacing: 4) {
                Text(post.title)
                    .font(.headline)
                    .foregroundColor(.white)
                    .lineLimit(2)
                HStack {
                    Text("By \(post.userId)")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.8))
                    
                    Spacer()
                    Button { } label: {
                        Image(systemName: "bookmark")
                            .padding(6)
                            .background(Color.white)
                            .clipShape(Circle())
                    }
                }
            }
            .padding(16)
        }
        .frame(maxWidth: .infinity, minHeight: 200, maxHeight: 200)
        .padding(.bottom, 4)
    }
}

struct MyProfileView_Previews: PreviewProvider {
    static var previews: some View {
        MyProfileView()
    }
}
