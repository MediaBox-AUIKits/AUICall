//
//  AUICallNVNMembersViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/7.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK

class AUICallNVMember {
    init(_ user: AUIRoomUser) {
        self.user = user
    }
    
    let user: AUIRoomUser
    var renderView: AUICallNVNRenderView? = nil
    var inviteUser: AUICallNVNInviteUser? = nil
}

public class AUICallNVNMembersViewController: AVBaseCollectionViewController {

    init(callController: AUICallNVNController) {
        self.callController = callController
        super.init(nibName: nil, bundle: nil)
        
        self.callController.addObserver(delegate: self)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        self.callController.removeObserver(delegate: self)
        debugPrint("deinit: \(self)")
    }
    
    let joinedCellIndentifier = "joined_cell"
    let waitingCellIndentifier = "waiting_cell"

    public override func viewDidLoad() {
        super.viewDidLoad()
        
        self.titleView.text = "成员列表"
        self.menuButton.isHidden = true
        self.collectionView.register(AUICallNVNMemberJoinedCell.self, forCellWithReuseIdentifier: joinedCellIndentifier)
        self.collectionView.register(AUICallNVNMemberInviteCell.self, forCellWithReuseIdentifier: waitingCellIndentifier)

        let top = self.contentView.av_height - UIView.av_safeBottom - 44.0
        if self.muteAllBtn != nil {
            let width = (self.contentView.av_width - 60.0) / 2.0
            self.inviteBtn.frame = CGRect(x: 20.0, y: top, width: width, height: 44.0)
            self.muteAllBtn!.frame = CGRect(x: width + 40.0, y: top, width: width, height: 44.0)
        }
        else {
            let width = self.contentView.av_width - 40.0
            self.inviteBtn.frame = CGRect(x: 20.0, y: top, width: width, height: 44.0)
        }
        self.collectionView.av_height = self.inviteBtn.av_top

        self.updateRenderViewList()
    }
    
    lazy var inviteBtn: UIButton = {
        let btn = UIButton()
        btn.layer.cornerRadius = 22
        btn.layer.borderWidth = 1
        btn.layer.borderColor = AVTheme.border_weak.cgColor
        btn.layer.masksToBounds = true
        btn.setTitle("邀请成员", for: .normal)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(16)
        btn.addTarget(self, action: #selector(onInviteBtnClicked), for: .touchUpInside)
        self.contentView.addSubview(btn)
        return btn
    }()
    
    lazy var muteAllBtn: UIButton? = {
        if self.callController.isAnchor {
            let btn = UIButton()
            btn.layer.cornerRadius = 22
            btn.layer.borderWidth = 1
            btn.layer.borderColor = AVTheme.border_weak.cgColor
            btn.layer.masksToBounds = true
            btn.isHighlighted = false
            btn.setTitle("全员静音", for:  .normal)
            btn.setTitle("解除全员静音", for: .selected)
            btn.setTitleColor(AVTheme.text_strong, for: .normal)
            btn.setTitleColor(AVTheme.danger_strong, for: .selected)
            btn.titleLabel?.font = AVTheme.regularFont(16)
            btn.addTarget(self, action: #selector(onMuteAllBtnClicked), for: .touchUpInside)
            btn.isSelected = self.callController.room.isMuteAudioAll
            self.contentView.addSubview(btn)
            return btn
        }
        return nil
    }()
    
    private let callController: AUICallNVNController
    private var renderViewList = [AUICallNVMember]()
    
    func updateRenderViewList() {
        self.renderViewList = [AUICallNVMember]()
        if let renderLayoutView = self.callController.renderLayoutView {
            renderLayoutView.getRenderViewList().forEach { renderView in
                let member = AUICallNVMember(renderView.user)
                member.renderView = renderView as? AUICallNVNRenderView
                self.renderViewList.append(member)
            }
        }
        self.callController.inviteUserList.forEach { inviteUser in
            let member = AUICallNVMember(inviteUser.user)
            member.inviteUser = inviteUser
            self.renderViewList.append(member)
        }
    }

    public override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        self.renderViewList.count
    }
    
    public override func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: self.collectionView.av_width, height: 66)
    }
    
    public override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let member = self.renderViewList[indexPath.row]
        if member.inviteUser != nil {
            let cell = self.collectionView.dequeueReusableCell(withReuseIdentifier: waitingCellIndentifier, for: indexPath) as! AUICallNVNMemberInviteCell
            cell.invitedUser = member.inviteUser
            cell.actionBtn.clickBlock = {[weak self, weak cell] btn in
                guard let self = self else { return }
                guard let invitedUser = cell?.invitedUser else { return }
                if invitedUser.state == .over {
                    self.callController.inviteUser(user: invitedUser.user)
                }
                else {
                    self.callController.cancelInviteUser(user: invitedUser.user)
                }
            }
            return cell
        }
        
        if member.renderView != nil {
            let cell = self.collectionView.dequeueReusableCell(withReuseIdentifier: joinedCellIndentifier, for: indexPath) as! AUICallNVNMemberJoinedCell
            cell.renderView = member.renderView
            cell.canRemove = self.callController.isAnchor
            cell.isUserInteractionEnabled = self.callController.isAnchor
            
            cell.removeBtn.clickBlock = {[weak self, weak cell] btn in
                guard let self = self else { return }
                guard let renderView = cell?.renderView else { return }
                AVAlertController.show(withTitle: "移除成员", message: "确定要移除 \(renderView.user.userNick)(id:\(renderView.user.userNick))吗？", cancelTitle: "取消", okTitle: "确定") { cancel in
                    if !cancel {
                        self.callController.removeUser(userId: renderView.user.userId)
                    }
                }
            }
            cell.muteAudioBtn.clickBlock = {[weak self, weak cell] btn in
                guard let self = self else { return }
                guard let renderView = cell?.renderView else { return }
                self.callController.muteAudio(mute: !btn.isSelected, uid: renderView.user.userId)
            }
            cell.muteVideoBtn.clickBlock = {[weak self, weak cell] btn in
                guard let self = self else { return }
                guard let renderView = cell?.renderView else { return }
                self.callController.muteVideo(mute: !btn.isSelected, uid: renderView.user.userId)
            }
            return cell
        }
        
        return UICollectionViewCell()
    }
}

extension AUICallNVNMembersViewController {
    
    @objc func onInviteBtnClicked() {
        let vc = AUICallNVNInviteViewController(callController: self.callController)
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    @objc func onMuteAllBtnClicked() {
        self.callController.muteAudioAll(mute: !self.muteAllBtn!.isSelected)
        self.muteAllBtn!.isSelected = !self.muteAllBtn!.isSelected
    }
}

extension AUICallNVNMembersViewController: AUICallNVNControllerDelegate {
    
    public func onCallUserMembersUpdated() {
        self.updateRenderViewList()
        self.collectionView.reloadData()
    }
    
    public func onCallMuteAudioAllChanged(off: Bool) {
        if self.callController.isAnchor {
            self.muteAllBtn?.isSelected = off
        }
    }
    
    public func onCallVideoMuteStateChanged(userId: String, off: Bool) {
        self.collectionView.visibleCells.forEach { cell in
            if let cell = cell as? AUICallNVNMemberJoinedCell {
                if cell.renderView?.user.userId == userId {
                    cell.refreshUI()
                }
            }
            
        }
    }
    
    public func onCallAudioMuteStateChanged(userId: String, off: Bool) {
        self.collectionView.visibleCells.forEach { cell in
            if let cell = cell as? AUICallNVNMemberJoinedCell {
                if cell.renderView?.user.userId == userId {
                    cell.refreshUI()
                }
            }
            
        }
    }
    
}
