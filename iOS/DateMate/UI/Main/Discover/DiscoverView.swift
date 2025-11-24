//
//  DiscoverView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/04.
//

import SwiftUI

struct DiscoverView: View{
    @StateObject var viewModel = DiscoverViewModel()
    
    var onNavigateToSubFirst: (String) -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 10) {
                VStack(alignment: .leading) {
                    Text("Recommended People for today")
                        .font(.system(size: 16, weight: .bold))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                    TodayRecommendSection(onNavigateToSubFirst: onNavigateToSubFirst)
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

struct TodayRecommendSection: View {
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
