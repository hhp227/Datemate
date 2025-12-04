//
//  DiscoverView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/04.
//

import SwiftUI

struct DiscoverView: View{
    @StateObject var viewModel: DiscoverViewModel = DependencyContainer.instance.provideDiscoverViewModel()
    
    var onNavigateToSubFirst: (String) -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 10) {
                VStack(alignment: .leading) {
                    Text("Recommended People for today")
                        .font(.system(size: 16, weight: .bold))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                    ZStack {
                        if viewModel.uiState.isLoading {
                            ProgressView()
                        } else if !viewModel.uiState.todayRecommendations.isEmpty {
                            TodayRecommendationPager(users: viewModel.uiState.todayRecommendations)
                        } else {
                            EmptyRecommendationView(
                                message: viewModel.uiState.message
                                ?? "아쉽게도 오늘은 추천 가능한 프로필이 없습니다. 내일 다시 확인해주세요!"
                            )
                            .frame(minHeight: UIScreen.main.bounds.height * 0.7)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(minHeight: UIScreen.main.bounds.height * 0.7)
                }
                VStack(alignment: .leading) {
                    Text("Today's Choice")
                        .font(.system(size: 16, weight: .bold))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                    TodaysChoiceSection(onNavigateToSubFirst: onNavigateToSubFirst)
                }
                VStack(alignment: .leading) {
                    Text("Famous People")
                        .font(.system(size: 16, weight: .bold))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                    ScrollView(.horizontal, showsIndicators: false) {
                        LazyHStack(spacing: 12) {
                            ForEach(0..<10) { i in
                                CardView(text: "111")
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
                VStack(alignment: .leading) {
                    Text("T.O.P Supporter")
                        .font(.system(size: 16, weight: .bold))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                    ScrollView(.horizontal, showsIndicators: false) {
                        LazyHStack(spacing: 12) {
                            ForEach(0..<10) { i in
                                CardView(text: "111")
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
                Button(action: {}) {
                    Text("Be a TOP Supporter")
                }
                .padding()
                Button(action: {}) {
                    Text("New Recommendation")
                }
                .padding()
            }
        }
    }
}

struct TodayRecommendationPager: View {
    let users: [Profile]

    var body: some View {
        VStack {
            TabView {
                ForEach(users.indices, id: \.self) { index in
                    DiscoverFullCard(
                        user: users[index],
                        onClick: {},
                        onLike: {},
                        onPass: {}
                    )
                    .frame(height: UIScreen.main.bounds.height * 0.7)
                    .padding(.horizontal, 20)
                }
            }
            .frame(height: UIScreen.main.bounds.height * 0.7)
            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            /*HStack {
                ForEach(users.indices, id: \.self) { i in
                    Circle()
                        .fill(i == 0 ? Color.pink : Color.gray.opacity(0.4))
                        .frame(width: 8, height: 8)
                }
            }
            .padding(.top, 10)*/
        }
    }
}

struct DiscoverFullCard: View {
    let user: Profile
    
    let onClick: () -> Void
    
    let onLike: () -> Void
    
    let onPass: () -> Void

    let primaryColor = Color(red: 1.0, green: 0.25, blue: 0.5)

    var body: some View {
        GeometryReader { proxy in
            let size = proxy.size
            
            ZStack {
                AsyncImage(url: URL(string: user.photos.first ?? "")) { phase in
                    switch phase {
                    case .success(let img):
                        img.resizable()
                            .scaledToFill()
                            .frame(width: size.width, height: size.height)
                            .clipped()
                    default:
                        Color.gray
                    }
                }
                .clipped()
                LinearGradient(
                    colors: [.clear, .clear, .black.opacity(0.8)],
                    startPoint: .top,
                    endPoint: .bottom
                )
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Text("Today's Pick")
                            .font(.caption.bold())
                            .padding(.horizontal, 10)
                            .padding(.vertical, 5)
                            .background(primaryColor)
                            .clipShape(Capsule())
                        Text(user.gender)
                            .font(.caption)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 5)
                            .overlay(
                                Capsule().stroke(Color.white, lineWidth: 1)
                            )
                    }
                    Text("\(user.name), \(user.ageFormatted)")
                        .font(.largeTitle.bold())
                        .foregroundColor(.white)
                    Text(user.job)
                        .foregroundColor(.white.opacity(0.9))
                    Text(user.bio.split(separator: "\n").first.map(String.init) ?? "")
                        .foregroundColor(.white.opacity(0.8))
                        .lineLimit(1)
                    Spacer().frame(height: 20)
                    HStack {
                        Button(action: onPass) {
                            Image(systemName: "xmark")
                                .font(.system(size: 32))
                                .frame(width: 56, height: 56)
                                .background(Color.white.opacity(0.2))
                                .clipShape(Circle())
                                .overlay(Circle().stroke(Color.white, lineWidth: 2))
                        }
                        Spacer()
                        Button(action: onLike) {
                            HStack {
                                Image(systemName: "heart.fill")
                                Text("좋아요")
                            }
                            .padding(.horizontal, 32)
                            .padding(.vertical, 12)
                            .foregroundColor(.white)
                            .background(primaryColor)
                            .cornerRadius(12)
                        }
                        .frame(height: 56)
                    }
                }
                .padding(24)
            }
            .frame(maxWidth: .infinity)
            .cornerRadius(12)
            .onTapGesture { onClick() }
            .shadow(radius: 8)
        }
    }
}

struct EmptyRecommendationView: View {
    let message: String

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "face.dashed")
                .font(.system(size: 64))
                .foregroundColor(.gray)
            Text("추천 프로필 없음")
                .font(.title3.bold())
            Text(message)
                .font(.subheadline)
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)
            Button("추천 기준 변경하기") {}
                .padding(.top, 24)
        }
        .padding(32)
    }
}

struct TodaysChoiceSection: View {
    var onNavigateToSubFirst: (String) -> Void
    
    var body: some View {
        let cardHeight = UIScreen.main.bounds.width / 1.5
        
        ZStack {
            RoundedRectangle(cornerRadius: 10)
                .fill(Color.white)
                .shadow(radius: 4)
            HStack(spacing: 0) {
                VStack {
                    Text("Left")
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color(.lightGray))
                .clipShape(RoundedCorners(tl: 10, tr: 0, bl: 10, br: 0))
                .onTapGesture {
                    onNavigateToSubFirst("Test")
                }
                VStack {
                    Text("Right")
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color.gray)
                .clipShape(RoundedCorners(tl: 0, tr: 10, bl: 0, br: 10))
                .onTapGesture {
                    // right click
                }
            }
        }
        .frame(height: cardHeight)
        .padding(.horizontal, 20)
    }
}

struct RoundedCorners: Shape {
    var tl: CGFloat = 0
    var tr: CGFloat = 0
    var bl: CGFloat = 0
    var br: CGFloat = 0

    func path(in rect: CGRect) -> Path {
        var path = Path()
        let width = rect.width
        let height = rect.height
        let tr = min(min(self.tr, height/2), width/2)
        let tl = min(min(self.tl, height/2), width/2)
        let bl = min(min(self.bl, height/2), width/2)
        let br = min(min(self.br, height/2), width/2)

        path.move(to: CGPoint(x: width / 2, y: 0))
        path.addLine(to: CGPoint(x: width - tr, y: 0))
        path.addArc(
            center: CGPoint(x: width - tr, y: tr),
            radius: tr,
            startAngle: .degrees(-90),
            endAngle: .degrees(0),
            clockwise: false
        )
        path.addLine(to: CGPoint(x: width, y: height - br))
        path.addArc(
            center: CGPoint(x: width - br, y: height - br),
            radius: br,
            startAngle: .degrees(0),
            endAngle: .degrees(90),
            clockwise: false
        )
        path.addLine(to: CGPoint(x: bl, y: height))
        path.addArc(
            center: CGPoint(x: bl, y: height - bl),
            radius: bl,
            startAngle: .degrees(90),
            endAngle: .degrees(180),
            clockwise: false
        )
        path.addLine(to: CGPoint(x: 0, y: tl))
        path.addArc(
            center: CGPoint(x: tl, y: tl),
            radius: tl,
            startAngle: .degrees(180),
            endAngle: .degrees(270),
            clockwise: false
        )
        return path
    }
}

struct CardView: View {
    let text: String
    
    var body: some View {
        let size = UIScreen.main.bounds.width / 3
        
        ZStack {
            RoundedRectangle(cornerRadius: 10)
                .fill(Color.blue)
                //.shadow(radius: 4)
            Text(text)
        }
        .frame(width: size, height: size)
    }
}

struct DiscoverView_Previews: PreviewProvider {
    static var previews: some View {
        DiscoverView(onNavigateToSubFirst: { _ in })
    }
}
